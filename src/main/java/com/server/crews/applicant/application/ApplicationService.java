package com.server.crews.applicant.application;

import static java.util.stream.Collectors.toSet;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.applicant.repository.NarrativeAnswerRepository;
import com.server.crews.applicant.repository.SelectiveAnswerRepository;
import com.server.crews.applicant.util.ApplicationMapper;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.repository.ApplicantRepository;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.NotFoundException;
import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.repository.ChoiceRepository;
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
    private final ApplicantRepository applicantRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final ChoiceRepository choiceRepository;
    private final SelectiveAnswerRepository selectiveAnswerRepository;
    private final NarrativeAnswerRepository narrativeAnswerRepository;

    @Transactional
    public ApplicationDetailsResponse saveApplication(Long applicantId, ApplicationSaveRequest request) {
        Recruitment recruitment = recruitmentRepository.findByCode(request.recruitmentCode())
                .orElseThrow(() -> new NotFoundException("모집 공고 코드", "모집 공고"));
        if (!recruitment.isStarted()) {
            throw new CrewsException(CrewsErrorCode.RECRUITMENT_NOT_STARTED);
        }
        if (!recruitment.isInProgress()) {
            throw new CrewsException(CrewsErrorCode.RECRUITMENT_CLOSED);
        }
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));

        validateNarrativeQuestions(request);
        validateSelectiveQuestions(request);

        Application application = ApplicationMapper.applicationSaveRequestToApplication(request, recruitment,
                applicant);
        Application savedApplication = applicationRepository.save(application);
        return ApplicationMapper.applicationToApplicationDetailsResponse(savedApplication);
    }

    private void validateNarrativeQuestions(ApplicationSaveRequest request) {
        List<AnswerSaveRequest> narrativeAnswerSaveRequests = filterByQuestionType(QuestionType.NARRATIVE, request);
        Set<Long> narrativeQuestionIds = extractQuestionIds(narrativeAnswerSaveRequests);
        List<NarrativeQuestion> savedNarrativeQuestions = narrativeQuestionRepository.findAllByIdIn(
                narrativeQuestionIds);
        validateQuestionIds(savedNarrativeQuestions, narrativeQuestionIds);
    }

    private void validateSelectiveQuestions(ApplicationSaveRequest request) {
        List<AnswerSaveRequest> selectiveAnswerSaveRequests = filterByQuestionType(QuestionType.SELECTIVE, request);
        Set<Long> selectiveQuestionIds = extractQuestionIds(selectiveAnswerSaveRequests);
        List<SelectiveQuestion> savedSelectiveQuestions = selectiveQuestionRepository.findAllByIdIn(
                selectiveQuestionIds);
        validateQuestionIds(savedSelectiveQuestions, selectiveQuestionIds);

        Set<Long> choiceIds = selectiveAnswerSaveRequests.stream()
                .map(AnswerSaveRequest::choiceId)
                .collect(toSet());
        List<Choice> savedChoices = choiceRepository.findAllByIdIn(choiceIds);
        if (savedChoices.size() != choiceIds.size()) {
            throw new NotFoundException("선택지 id", "선택지");
        }
    }

    private List<AnswerSaveRequest> filterByQuestionType(QuestionType questionType,
                                                         ApplicationSaveRequest applicationSaveRequest) {
        return applicationSaveRequest.answers().stream()
                .filter(answerSaveRequest -> questionType.hasSameName(answerSaveRequest.questionType()))
                .toList();
    }

    private Set<Long> extractQuestionIds(List<AnswerSaveRequest> answerSaveRequests) {
        return answerSaveRequests.stream()
                .map(AnswerSaveRequest::questionId)
                .collect(toSet());
    }

    private void validateQuestionIds(List<?> savedQuestions, Set<Long> questionIds) {
        if (savedQuestions.size() != questionIds.size()) {
            throw new NotFoundException("질문 id", "질문");
        }
    }

    public List<ApplicationsResponse> findAllApplicationsByPublisher(Long publisherId) {
        List<Application> applications = applicationRepository.findAllWithRecruitmentByPublisherId(publisherId);
        return applications.stream()
                .map(ApplicationMapper::applicationToApplicationsResponse)
                .toList();
    }

    public ApplicationDetailsResponse findApplicationDetails(Long applicationId, Long publisherId) {
        Application application = applicationRepository.findByIdWithRecruitmentAndPublisher(applicationId)
                .orElseThrow(() -> new NotFoundException("지원서 id", "지원서"));
        List<NarrativeAnswer> narrativeAnswers = narrativeAnswerRepository.findAllByApplication(application);
        List<SelectiveAnswer> selectiveAnswers = selectiveAnswerRepository.findAllByApplication(application);
        application.replaceNarrativeAnswers(narrativeAnswers);
        application.replaceSelectiveAnswers(selectiveAnswers);
        return ApplicationMapper.applicationToApplicationDetailsResponse(application);
    }

    public Optional<ApplicationDetailsResponse> findMyApplicationDetails(Long applicantId, String code) {
        return applicationRepository.findByApplicantIdAndRecruitmentCode(applicantId, code)
                .map(application -> {
                    List<NarrativeAnswer> narrativeAnswers = narrativeAnswerRepository.findAllByApplication(
                            application);
                    List<SelectiveAnswer> selectiveAnswers = selectiveAnswerRepository.findAllByApplication(
                            application);
                    application.replaceNarrativeAnswers(narrativeAnswers);
                    application.replaceSelectiveAnswers(selectiveAnswers);
                    return ApplicationMapper.applicationToApplicationDetailsResponse(application);
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
