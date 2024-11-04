package com.server.crews.applicant.service;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.applicant.mapper.ApplicationMapper;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.service.RecruitmentDetailsLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationDetailsLoader applicationDetailsLoader;
    private final RecruitmentDetailsLoader recruitmentDetailsLoader;

    @Transactional
    public ApplicationDetailsResponse saveApplication(Long applicantId, ApplicationSaveRequest request) {
        Recruitment recruitment = recruitmentDetailsLoader.findWithSectionsByCode(request.recruitmentCode());
        validateRecruitmentProgress(recruitment);

        ApplicationManager applicationManager = applicationRepository.findByApplicantId(applicantId)
                .map(previosApplication -> new ApplicationManager(recruitment, previosApplication))
                .orElse(new ApplicationManager(recruitment));

        List<NarrativeAnswer> newNarrativeAnswers = ApplicationMapper.narrativeAnswersInApplicationSaveRequest(request);
        List<SelectiveAnswer> newSelectiveAnswers = ApplicationMapper.selectiveAnswersInApplicationSaveRequest(request);

        List<NarrativeAnswer> updatedNarrativeAnswers = applicationManager.writeNarrativeAnswers(newNarrativeAnswers);
        List<SelectiveAnswer> updatedSelectiveAnswers = applicationManager.writeSelectiveAnswers(newSelectiveAnswers);

        Application application = ApplicationMapper.applicationSaveRequestToApplication(request, recruitment,
                applicantId, updatedNarrativeAnswers, updatedSelectiveAnswers);
        Application savedApplication = applicationRepository.save(application);

        return ApplicationAnswerReader.readBySection(recruitment, savedApplication);
    }

    private void validateRecruitmentProgress(Recruitment recruitment) {
        if (!recruitment.isStarted()) {
            throw new CrewsException(CrewsErrorCode.RECRUITMENT_NOT_STARTED);
        }
        if (!recruitment.isInProgress()) {
            throw new CrewsException(CrewsErrorCode.RECRUITMENT_CLOSED);
        }
    }

    public List<ApplicationsResponse> findAllApplicationsByPublisher(Long publisherId) {
        List<Application> applications = applicationRepository.findAllWithRecruitmentByPublisherId(publisherId);
        return applications.stream()
                .map(ApplicationMapper::applicationToApplicationsResponse)
                .toList();
    }

    public ApplicationDetailsResponse findApplicationDetails(Long applicationId, Long publisherId) {
        Recruitment recruitment = recruitmentDetailsLoader.findWithSectionsByPublisherId(publisherId);

        Application application = applicationDetailsLoader.findByIdWithRecruitmentAndPublisher(applicationId);

        return ApplicationAnswerReader.readBySection(recruitment, application);
    }

    public Optional<ApplicationDetailsResponse> findMyApplicationDetails(Long applicantId, String code) {
        Recruitment recruitment = recruitmentDetailsLoader.findWithSectionsByCode(code);
        return applicationDetailsLoader.findNullableByApplicantIdAndRecruitmentCode(applicantId, code)
                .map(application -> ApplicationAnswerReader.readBySection(recruitment, application));
    }

    @Transactional
    public void decideOutcome(EvaluationRequest request, Long publisherId) {
        List<Application> applications = applicationRepository.findAllWithRecruitmentByPublisherId(publisherId);
        applications.stream().findAny()
                .map(Application::getRecruitment)
                .ifPresent(this::checkRecruitmentAnnouncedProgress);

        Set<Long> passApplicationIds = new HashSet<>(request.passApplicationIds());
        applications.stream()
                .filter(application -> containedInPassList(application, passApplicationIds))
                .forEach(Application::pass);
        applications.stream()
                .filter(application -> !containedInPassList(application, passApplicationIds))
                .forEach(Application::reject);
    }

    private void checkRecruitmentAnnouncedProgress(Recruitment recruitment) {
        if (recruitment.isAnnounced()) {
            throw new CrewsException(CrewsErrorCode.ALREADY_ANNOUNCED);
        }
    }

    private boolean containedInPassList(Application application, Set<Long> passApplicationIds) {
        return passApplicationIds.contains(application.getId());
    }
}
