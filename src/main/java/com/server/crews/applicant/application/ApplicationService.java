package com.server.crews.applicant.application;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.applicant.repository.NarrativeAnswerRepository;
import com.server.crews.applicant.repository.SelectiveAnswerRepository;
import com.server.crews.applicant.util.ApplicationMapper;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.NotFoundException;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
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
    private final RecruitmentRepository recruitmentRepository;
    private final ApplicationRepository applicationRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final SelectiveAnswerRepository selectiveAnswerRepository;
    private final NarrativeAnswerRepository narrativeAnswerRepository;

    @Transactional
    public ApplicationDetailsResponse saveApplication(Long applicantId, ApplicationSaveRequest request) {
        Recruitment recruitment = recruitmentRepository.findByCode(request.recruitmentCode())
                .orElseThrow(() -> new NotFoundException("모집 공고 코드", "모집 공고"));
        validateRecruitmentProgress(recruitment);

        Long recruitmentId = recruitment.getId();
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllByRecruitmentId(recruitmentId);
        List<SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllByRecruitmentId(recruitmentId);
        ApplicationForm applicationForm = applicationRepository.findByApplicantId(applicantId)
                .map(previosApplication ->
                        new ApplicationForm(narrativeQuestions, selectiveQuestions, previosApplication))
                .orElse(new ApplicationForm(narrativeQuestions, selectiveQuestions));

        List<NarrativeAnswer> newNarrativeAnswers = ApplicationMapper.narrativeAnswersInApplicationSaveRequest(request);
        List<SelectiveAnswer> newSelectiveAnswers = ApplicationMapper.selectiveAnswersInApplicationSaveRequest(request);

        List<NarrativeAnswer> updatedNarrativeAnswers = applicationForm.writeNarrativeAnswers(newNarrativeAnswers);
        List<SelectiveAnswer> updatedSelectiveAnswers = applicationForm.writeSelectiveAnswers(newSelectiveAnswers);

        Application application = ApplicationMapper.applicationSaveRequestToApplication(request, recruitment,
                applicantId, updatedNarrativeAnswers, updatedSelectiveAnswers);
        Application savedApplication = applicationRepository.save(application);

        ApplicationAnswerReader applicationAnswerReader = new ApplicationAnswerReader(narrativeQuestions,
                selectiveQuestions, savedApplication);
        return applicationAnswerReader.readBySection();
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
        Recruitment recruitment = recruitmentRepository.findByPublisher(publisherId)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        Long recruitmentId = recruitment.getId();
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllByRecruitmentId(recruitmentId);
        List<SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllByRecruitmentId(recruitmentId);

        Application application = applicationRepository.findByIdWithRecruitmentAndPublisher(applicationId)
                .orElseThrow(() -> new NotFoundException("지원서 id", "지원서"));
        List<NarrativeAnswer> narrativeAnswers = narrativeAnswerRepository.findAllByApplication(application);
        List<SelectiveAnswer> selectiveAnswers = selectiveAnswerRepository.findAllByApplication(application);
        application.replaceNarrativeAnswers(narrativeAnswers);
        application.replaceSelectiveAnswers(selectiveAnswers);

        ApplicationAnswerReader applicationAnswerReader = new ApplicationAnswerReader(narrativeQuestions,
                selectiveQuestions, application);
        return applicationAnswerReader.readBySection();
    }

    public Optional<ApplicationDetailsResponse> findMyApplicationDetails(Long applicantId, String code) {
        Recruitment recruitment = recruitmentRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        Long recruitmentId = recruitment.getId();
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllByRecruitmentId(recruitmentId);
        List<SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllByRecruitmentId(recruitmentId);

        return applicationRepository.findByApplicantIdAndRecruitmentCode(applicantId, code)
                .map(application -> {
                    List<NarrativeAnswer> narrativeAnswers = narrativeAnswerRepository.findAllByApplication(
                            application);
                    List<SelectiveAnswer> selectiveAnswers = selectiveAnswerRepository.findAllByApplication(
                            application);
                    application.replaceNarrativeAnswers(narrativeAnswers);
                    application.replaceSelectiveAnswers(selectiveAnswers);

                    ApplicationAnswerReader applicationAnswerReader = new ApplicationAnswerReader(narrativeQuestions,
                            selectiveQuestions, application);
                    return applicationAnswerReader.readBySection();
                });
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
