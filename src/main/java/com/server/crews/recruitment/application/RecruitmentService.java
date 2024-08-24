package com.server.crews.recruitment.application;

import static java.util.stream.Collectors.groupingBy;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.RecruitmentStateInProgressResponse;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Transactional
    public RecruitmentDetailsResponse saveRecruitment(Long publisherId, RecruitmentSaveRequest request) {
        Administrator publisher = administratorRepository.findById(publisherId)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));
        String code = UUID.randomUUID().toString();
        Recruitment recruitment = request.toRecruitment(code, publisher);
        return RecruitmentDetailsResponse.from(recruitmentRepository.save(recruitment));
    }

    @Transactional
    public void startRecruiting(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        if (recruitment.isStarted()) {
            throw new CrewsException(ErrorCode.RECRUITMENT_ALREADY_STARTED);
        }
        recruitment.start();
    }

    public RecruitmentStateInProgressResponse findRecruitmentStateInProgress(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        int applicationCount = applicationRepository.countAllByRecruitment(recruitment);
        return new RecruitmentStateInProgressResponse(applicationCount, recruitment.getDeadline());
    }

    public RecruitmentDetailsResponse findRecruitmentDetailsById(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsById(recruitmentId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        return findRecruitmentDetails(recruitment);
    }

    public RecruitmentDetailsResponse findRecruitmentDetailsByCode(String code) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsByCode(code)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        return findRecruitmentDetails(recruitment);
    }

    public RecruitmentDetailsResponse findRecruitmentDetails(Recruitment recruitment) {
        List<Section> sections = recruitment.getSections();
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllBySectionIn(sections);
        List<SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllWithChoicesInSections(sections);

        Map<Section, List<NarrativeQuestion>> narrativeQuestionsBySection = narrativeQuestions.stream()
                .collect(groupingBy(NarrativeQuestion::getSection));
        Map<Section, List<SelectiveQuestion>> selectiveQuestionsBySection = selectiveQuestions.stream()
                .collect(groupingBy(SelectiveQuestion::getSection));
        return RecruitmentDetailsResponse.from(recruitment, narrativeQuestionsBySection, selectiveQuestionsBySection);
    }

    @Transactional
    public void updateDeadline(Long publisherId, DeadlineUpdateRequest request) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        LocalDateTime deadline = LocalDateTime.parse(request.deadline());
        recruitment.updateDeadline(deadline);
    }

    @Transactional
    @Scheduled(cron = "${schedules.cron.closing-recruitment}")
    public void closeRecruitments() {
        LocalDateTime now = LocalDateTime.now();
        List<Recruitment> recruitments = recruitmentRepository.findAll();
        recruitments.stream()
                .filter(recruitment -> recruitment.hasPassedDeadline(now))
                .forEach(Recruitment::close);
    }

    @Transactional
    public void announceRecruitmentOutcome(Long adminId) {
        Recruitment recruitment = recruitmentRepository.findWithPublisherByPublisher(adminId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        if (recruitment.isAnnounced()) {
            throw new CrewsException(ErrorCode.ALREADY_ANNOUNCED);
        }
        List<Application> applications = applicationRepository.findAllWithApplicantByPublisherId(recruitment.getId());
        applications.stream().filter(Application::isNotDetermined)
                .forEach(Application::reject);

        eventPublisher.publishEvent(new OutcomeDeterminedEvent(applications, recruitment));
        recruitment.announce();
    }
}
