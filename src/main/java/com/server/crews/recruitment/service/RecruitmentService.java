package com.server.crews.recruitment.service;

import static java.util.stream.Collectors.joining;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.global.CustomLogger;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.NotFoundException;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.RecruitmentProgress;
import com.server.crews.recruitment.dto.response.RecruitmentSearchResponse;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.RecruitmentProgressResponse;
import com.server.crews.recruitment.dto.response.RecruitmentStateInProgressResponse;
import com.server.crews.recruitment.mapper.RecruitmentMapper;
import com.server.crews.recruitment.repository.RecruitmentSearchCacheStore;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentDetailsLoader recruitmentDetailsLoader;
    private final RecruitmentSearchCacheStore recruitmentSearchCacheStore;
    private final AdministratorRepository administratorRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;
    private final CustomLogger customLogger = new CustomLogger(RecruitmentService.class);

    @Transactional
    public RecruitmentDetailsResponse saveRecruitment(Long publisherId, RecruitmentSaveRequest request) {
        Administrator publisher = administratorRepository.findById(publisherId)
                .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));
        Recruitment recruitment = RecruitmentMapper.recruitmentSaveRequestToRecruitment(request, publisher);
        setCode(recruitment);
        validateDeadline(recruitment.getDeadline());
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return RecruitmentMapper.recruitmentToRecruitmentDetailsResponse(savedRecruitment);
    }

    private void setCode(Recruitment recruitment) {
        if (recruitment.getCode() == null) {
            String code = UUID.randomUUID().toString();
            recruitment.setCode(code);
        }
    }

    private void validateDeadline(LocalDateTime deadline) {
        LocalDateTime now = LocalDateTime.now(Clock.system(ZoneId.of("Asia/Seoul")));
        if (deadline.isBefore(now)) {
            throw new CrewsException(CrewsErrorCode.INVALID_DEADLINE);
        }
        if (deadline.getMinute() != 0 || deadline.getSecond() != 0 || deadline.getNano() != 0) {
            throw new CrewsException(CrewsErrorCode.INVALID_DEADLINE);
        }
    }

    public List<RecruitmentSearchResponse> searchRecruitmentsTitle(String prefix, int limit) {
        List<String> recruitmentCodes = recruitmentSearchCacheStore.findRecruitmentTitlesByPrefix(prefix, limit);
        return recruitmentCodes.stream()
                .map(RecruitmentSearchResponse::new)
                .toList();
    }

    @Transactional
    public void startRecruiting(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        if (recruitment.isStarted()) {
            throw new CrewsException(CrewsErrorCode.RECRUITMENT_ALREADY_STARTED);
        }
        recruitment.start();
        recruitmentSearchCacheStore.saveRecruitment(recruitment);
    }

    public RecruitmentStateInProgressResponse findRecruitmentStateInProgress(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        int applicationCount = applicationRepository.countAllByRecruitment(recruitment);
        return new RecruitmentStateInProgressResponse(applicationCount, recruitment.getDeadline(),
                recruitment.getCode());
    }

    public Optional<RecruitmentDetailsResponse> findRecruitmentDetailsInReady(Long publisherId) {
        return recruitmentDetailsLoader.findNullableWithSectionsByPublisherId(publisherId)
                .map(RecruitmentMapper::recruitmentToRecruitmentDetailsResponse);
    }

    public RecruitmentDetailsResponse findRecruitmentDetailsByCode(String code) {
        Recruitment recruitment = recruitmentDetailsLoader.findWithSectionsByCode(code);
        if (!recruitment.isStarted()) {
            throw new CrewsException(CrewsErrorCode.RECRUITMENT_NOT_STARTED);
        }
        return RecruitmentMapper.recruitmentToRecruitmentDetailsResponse(recruitment);
    }

    public RecruitmentDetailsResponse findRecruitmentDetailsByTitle(String title) {
        Recruitment recruitment = recruitmentDetailsLoader.findWithSectionsByTitle(title);
        if (!recruitment.isStarted()) {
            throw new CrewsException(CrewsErrorCode.RECRUITMENT_NOT_STARTED);
        }
        return RecruitmentMapper.recruitmentToRecruitmentDetailsResponse(recruitment);
    }

    public RecruitmentProgressResponse findRecruitmentProgress(Long publisherId) {
        return recruitmentRepository.findByPublisher(publisherId)
                .map(Recruitment::getProgress)
                .map(RecruitmentProgressResponse::new)
                .orElse(new RecruitmentProgressResponse(RecruitmentProgress.READY));
    }

    @Transactional
    public void updateDeadline(Long publisherId, DeadlineUpdateRequest request) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        LocalDateTime modifiedDeadline = request.deadline();
        if (!recruitment.hasOnOrAfterDeadline(modifiedDeadline) || !recruitment.isInProgress()) {
            throw new CrewsException(CrewsErrorCode.INVALID_MODIFIED_DEADLINE);
        }
        recruitment.updateDeadline(modifiedDeadline);
    }

    @Transactional
    @Scheduled(cron = "${schedules.cron.closing-recruitment}")
    public void closeRecruitments() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Recruitment> recruitments = recruitmentRepository.findAll();
        List<Recruitment> recruitmentsToBeClosed = recruitments.stream()
                .filter(recruitment -> recruitment.hasOnOrAfterDeadline(now))
                .toList();
        recruitmentsToBeClosed.forEach(Recruitment::close);
        String closedRecruitmentIds = recruitmentsToBeClosed.stream()
                .map(Recruitment::getId)
                .map(String::valueOf)
                .collect(joining(" "));
        customLogger.info("closeRecruitments - closedRecruitmentIds: {}", closedRecruitmentIds);
    }

    @Transactional
    public void announceRecruitmentOutcome(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findWithPublisherByPublisher(publisherId)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        if (recruitment.isAnnounced()) {
            throw new CrewsException(CrewsErrorCode.ALREADY_ANNOUNCED);
        }
        List<Application> applications = applicationRepository.findAllByRecruitmentWithApplicant(recruitment);
        applications.stream().filter(Application::isNotDetermined)
                .forEach(Application::reject);

        eventPublisher.publishEvent(new OutcomeDeterminedEvent(applications, recruitment));
        recruitment.announce();
    }
}
