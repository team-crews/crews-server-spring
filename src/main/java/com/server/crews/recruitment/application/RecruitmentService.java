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
import com.server.crews.recruitment.dto.request.ClosingDateUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
        recruitmentRepository.findByPublisher(publisherId)
                .ifPresent(existingRecruitment -> recruitment.setByExistingId(existingRecruitment.getId()));
        recruitmentRepository.save(recruitment);
        return RecruitmentDetailsResponse.from(recruitment);
    }

    @Transactional
    public void startRecruiting(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        recruitment.start();
    }

    public RecruitmentDetailsResponse findRecruitmentDetails(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsById(recruitmentId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
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
    public void updateClosingDate(Long recruitmentId, ClosingDateUpdateRequest request) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        recruitment.updateClosingDate(request.closingDate());
    }

    @Transactional
    public void sendOutcomeEmail(Long adminId) {
        Recruitment recruitment = recruitmentRepository.findByPublisher(adminId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        List<Application> applications = applicationRepository.findAllWithApplicantByRecruitmentId(recruitment.getId());

        applications.stream().filter(Application::isNotDetermined)
                .forEach(Application::reject);

        eventPublisher.publishEvent(new OutcomeDeterminedEvent(applications, recruitment));
    }
}
