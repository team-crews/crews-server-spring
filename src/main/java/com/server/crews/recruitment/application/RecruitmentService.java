package com.server.crews.recruitment.application;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.global.CustomLogger;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.GeneralErrorCode;
import com.server.crews.global.exception.NotFoundException;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.RecruitmentProgress;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.RecruitmentProgressResponse;
import com.server.crews.recruitment.dto.response.RecruitmentStateInProgressResponse;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
import com.server.crews.recruitment.util.QuestionSorter;
import com.server.crews.recruitment.util.RecruitmentMapper;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
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
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final AdministratorRepository administratorRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;
    private final CustomLogger customLogger = new CustomLogger(RecruitmentService.class);

    @Transactional
    public RecruitmentDetailsResponse saveRecruitment(Long publisherId, RecruitmentSaveRequest request) {
        Administrator publisher = administratorRepository.findById(publisherId)
                .orElseThrow(() -> new CrewsException(GeneralErrorCode.USER_NOT_FOUND));
        String code = UUID.randomUUID().toString();
        Recruitment recruitment = RecruitmentMapper.recruitmentSaveRequestToRecruitment(request, code, publisher);
        validateDeadline(recruitment.getDeadline());
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        RecruitmentDetailsResponse recruitmentDetailsResponse = RecruitmentMapper.recruitmentToRecruitmentDetailsResponse(
                savedRecruitment);
        QuestionSorter.sort(recruitmentDetailsResponse);
        return recruitmentDetailsResponse;
    }

    private void validateDeadline(LocalDateTime deadline) {
        LocalDateTime now = LocalDateTime.now(Clock.system(ZoneId.of("Asia/Seoul")));
        if (deadline.isBefore(now)) {
            throw new CrewsException(GeneralErrorCode.INVALID_DEADLINE);
        }
        if (deadline.getMinute() != 0 || deadline.getSecond() != 0 || deadline.getNano() != 0) {
            throw new CrewsException(GeneralErrorCode.INVALID_DEADLINE);
        }
    }

    @Transactional
    public void startRecruiting(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        if (recruitment.isStarted()) {
            throw new CrewsException(GeneralErrorCode.RECRUITMENT_ALREADY_STARTED);
        }
        recruitment.start();
    }

    public RecruitmentStateInProgressResponse findRecruitmentStateInProgress(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        int applicationCount = applicationRepository.countAllByRecruitment(recruitment);
        return new RecruitmentStateInProgressResponse(applicationCount, recruitment.getDeadline(),
                recruitment.getCode());
    }

    public Optional<RecruitmentDetailsResponse> findRecruitmentDetailsInReady(Long publisherId) {
        return recruitmentRepository.findWithSectionsByPublisherId(publisherId)
                .map(this::toRecruitmentDetailsWithQuestions);
    }

    public RecruitmentDetailsResponse findRecruitmentDetailsByCode(String code) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsByCode(code)
                .orElseThrow(() -> new NotFoundException("모집 공고 코드", "모집 공고"));
        if (!recruitment.isStarted()) {
            throw new CrewsException(GeneralErrorCode.RECRUITMENT_NOT_STARTED);
        }
        return toRecruitmentDetailsWithQuestions(recruitment);
    }

    public RecruitmentDetailsResponse toRecruitmentDetailsWithQuestions(Recruitment recruitment) {
        List<Section> sections = recruitment.getSections();
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllBySectionIn(sections);
        List<SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllWithChoicesInSections(sections);
        Map<Section, List<NarrativeQuestion>> narrativeQuestionsBySection = narrativeQuestions.stream()
                .collect(groupingBy(NarrativeQuestion::getSection));
        Map<Section, List<SelectiveQuestion>> selectiveQuestionsBySection = selectiveQuestions.stream()
                .collect(groupingBy(SelectiveQuestion::getSection));
        sections.forEach(section -> {
            List<NarrativeQuestion> narratives = narrativeQuestionsBySection.getOrDefault(section, List.of());
            List<SelectiveQuestion> selectives = selectiveQuestionsBySection.getOrDefault(section, List.of());
            section.replaceQuestions(narratives, selectives);
        });
        RecruitmentDetailsResponse recruitmentDetailsResponse = RecruitmentMapper.recruitmentToRecruitmentDetailsResponse(
                recruitment);
        QuestionSorter.sort(recruitmentDetailsResponse);
        return recruitmentDetailsResponse;
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

        LocalDateTime modifiedDeadline = LocalDateTime.parse(request.deadline());
        if (!recruitment.hasOnOrAfterDeadline(modifiedDeadline) || !recruitment.isInProgress()) {
            throw new CrewsException(GeneralErrorCode.INVALID_MODIFIED_DEADLINE);
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
            throw new CrewsException(GeneralErrorCode.ALREADY_ANNOUNCED);
        }
        List<Application> applications = applicationRepository.findAllByRecruitmentWithApplicant(recruitment);
        applications.stream().filter(Application::isNotDetermined)
                .forEach(Application::reject);

        eventPublisher.publishEvent(new OutcomeDeterminedEvent(applications, recruitment));
        recruitment.announce();
    }
}
