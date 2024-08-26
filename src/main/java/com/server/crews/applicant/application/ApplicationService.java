package com.server.crews.applicant.application;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
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
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.repository.ApplicantRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
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
import java.util.Map;
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
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));

        List<NarrativeAnswer> narrativeAnswers = extractNarrativeAnswers(request);
        List<SelectiveAnswer> selectiveAnswers = extractSelectiveAnswers(request);

        Application application = new Application(request.id(), recruitment, applicant, request.studentNumber(),
                request.major(), request.name(), narrativeAnswers, selectiveAnswers);

        Application savedApplication = applicationRepository.save(application);

        return ApplicationDetailsResponse.of(savedApplication, narrativeAnswers,
                collectSelectiveAnswersByQuestion(selectiveAnswers));
    }

    private List<NarrativeAnswer> extractNarrativeAnswers(ApplicationSaveRequest request) {
        List<AnswerSaveRequest> narrativeAnswerSaveRequests = filterByQuestionType(QuestionType.NARRATIVE, request);
        Set<Long> narrativeQuestionIds = extractQuestionIds(narrativeAnswerSaveRequests);
        if (narrativeQuestionIds.size() != narrativeAnswerSaveRequests.size()) {
            throw new CrewsException(ErrorCode.DUPLICATE_NARRATIVE_ANSWERS);
        }
        List<NarrativeQuestion> savedNarrativeQuestions = narrativeQuestionRepository.findAllByIdIn(
                narrativeQuestionIds);
        validateQuestionIds(savedNarrativeQuestions, narrativeQuestionIds);

        Map<Long, NarrativeQuestion> savedNarrativeQuestionsById = savedNarrativeQuestions.stream()
                .collect(toMap(NarrativeQuestion::getId, identity()));

        return narrativeAnswerSaveRequests.stream()
                .map(narrativeQuestionAnswerRequest -> new NarrativeAnswer(narrativeQuestionAnswerRequest.answerId(),
                        savedNarrativeQuestionsById.get(narrativeQuestionAnswerRequest.questionId()),
                        narrativeQuestionAnswerRequest.content()))
                .toList();
    }

    private List<SelectiveAnswer> extractSelectiveAnswers(ApplicationSaveRequest request) {
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
            throw new CrewsException(ErrorCode.CHOICE_NOT_FOUND);
        }

        Map<Long, Choice> savedChoicesById = savedChoices.stream()
                .collect(toMap(Choice::getId, identity()));
        Map<Long, SelectiveQuestion> savedSelectiveQuestionsById = savedSelectiveQuestions.stream()
                .collect(toMap(SelectiveQuestion::getId, identity()));

        return selectiveAnswerSaveRequests.stream()
                .map(selectiveQuestionAnswerRequest -> new SelectiveAnswer(selectiveQuestionAnswerRequest.answerId(),
                        savedChoicesById.get(selectiveQuestionAnswerRequest.choiceId()),
                        savedSelectiveQuestionsById.get(selectiveQuestionAnswerRequest.questionId()))).toList();
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
            throw new CrewsException(ErrorCode.QUESTION_NOT_FOUND);
        }
    }

    public List<ApplicationsResponse> findAllApplicationsByRecruitment(Long publisherId) {
        List<Application> applications = applicationRepository.findAllWithApplicantByPublisherId(publisherId);
        return applications.stream()
                .map(ApplicationsResponse::from)
                .toList();
    }

    public ApplicationDetailsResponse findApplicationDetails(Long applicationId, Long publisherId) {
        Application application = applicationRepository.findByIdWithRecruitmentAndPublisher(applicationId)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
        checkPermission(application, publisherId);
        List<NarrativeAnswer> narrativeAnswers = narrativeAnswerRepository.findAllByApplication(application);
        Map<Long, List<SelectiveAnswer>> selectiveAnswers = collectSelectiveAnswersByQuestion(
                selectiveAnswerRepository.findAllByApplication(application));
        return ApplicationDetailsResponse.of(application, narrativeAnswers, selectiveAnswers);
    }

    private void checkPermission(Application application, Long publisherId) {
        if (!application.canBeAccessedBy(publisherId)) {
            throw new CrewsException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    private Map<Long, List<SelectiveAnswer>> collectSelectiveAnswersByQuestion(List<SelectiveAnswer> selectiveAnswers) {
        return selectiveAnswers.stream()
                .collect(groupingBy(selectiveAnswer -> selectiveAnswer.getSelectiveQuestion().getId()));
    }

    @Transactional
    public void decideOutcome(EvaluationRequest request, Long publisherId) {
        List<Application> applications = applicationRepository.findAllWithApplicantByPublisherId(publisherId);
        Set<Long> passApplicationIds = new HashSet<>(request.passApplicationIds());

        applications.stream()
                .filter(application -> containedInPassList(application, passApplicationIds))
                .forEach(Application::pass);
        applications.stream()
                .filter(application -> !containedInPassList(application, passApplicationIds))
                .forEach(Application::reject);
    }

    private boolean containedInPassList(Application application, Set<Long> passApplicationIds) {
        return passApplicationIds.contains(application.getId());
    }
}
