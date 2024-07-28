package com.server.crews.applicant.application;

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
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.repository.ChoiceRepository;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final ChoiceRepository choiceRepository;
    private final SelectiveAnswerRepository selectiveAnswerRepository;
    private final NarrativeAnswerRepository narrativeAnswerRepository;

    @Transactional
    public ApplicationDetailsResponse createApplication(Long applicantId, ApplicationSaveRequest request) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));

        Map<Long, AnswerSaveRequest> answerSaveRequestsByQuestionId = request.answers().stream()
                .collect(toMap(AnswerSaveRequest::questionId, Function.identity()));
        List<SelectiveAnswer> selectiveAnswers = toSelectiveAnswers(answerSaveRequestsByQuestionId);
        List<NarrativeAnswer> narrativeAnswers = toNarrativeAnswers(answerSaveRequestsByQuestionId);
        Application application = new Application(applicant, request.studentNumber(), request.major(), request.name(),
                narrativeAnswers, selectiveAnswers);

        applicationRepository.save(application);

        return ApplicationDetailsResponse.of(application, narrativeAnswers, collectSelectiveAnswersByQuestion(selectiveAnswers));
    }

    private List<SelectiveAnswer> toSelectiveAnswers(Map<Long, AnswerSaveRequest> answerSaveRequestsByQuestionId) {
        List<Long> selectiveQuestionIds = answerSaveRequestsByQuestionId.values().stream()
                .filter(AnswerSaveRequest::isSelective)
                .map(AnswerSaveRequest::questionId)
                .toList();
        Map<Long, SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllByIdIn(selectiveQuestionIds)
                .stream()
                .collect(toMap(SelectiveQuestion::getId, Function.identity()));
        validateQuestionIds(selectiveQuestionIds.size(), selectiveQuestions.size());
        List<Choice> choices = toChoices(answerSaveRequestsByQuestionId.values());
        return choices.stream()
                .map(choice -> new SelectiveAnswer(choice, selectiveQuestions.get(choice.getSelectiveQuestion().getId())))
                .toList();
    }

    private List<Choice> toChoices(Collection<AnswerSaveRequest> answerSaveRequests) {
        List<Long> choiceIds = answerSaveRequests.stream()
                .map(AnswerSaveRequest::choiceIds)
                .flatMap(List::stream)
                .toList();
        List<Choice> choices = choiceRepository.findAllByIdIn(choiceIds);
        validateChoiceIds(choiceIds.size(), choices.size());
        return choices;
    }

    private void validateChoiceIds(int choiceIdsSize, int foundChoiceCount) {
        if (choiceIdsSize != foundChoiceCount) {
            throw new CrewsException(ErrorCode.CHOICE_NOT_FOUND);
        }
    }

    private List<NarrativeAnswer> toNarrativeAnswers(Map<Long, AnswerSaveRequest> answerSaveRequestsByQuestionId) {
        List<Long> narrativeQuestionIds = answerSaveRequestsByQuestionId.values().stream()
                .filter(AnswerSaveRequest::isNarrative)
                .map(AnswerSaveRequest::questionId)
                .toList();
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllByIdIn(narrativeQuestionIds);
        validateQuestionIds(narrativeQuestionIds.size(), narrativeQuestions.size());
        return narrativeQuestions.stream()
                .map(question -> new NarrativeAnswer(question, answerSaveRequestsByQuestionId.get(question.getId()).content()))
                .toList();
    }

    private void validateQuestionIds(int questionIdCount, int foundQuestionCount) {
        if (questionIdCount != foundQuestionCount) {
            throw new CrewsException(ErrorCode.QUESTION_NOT_FOUND);
        }
    }

    public List<ApplicationsResponse> findAllApplicationsByRecruitment(Long recruitmentId) {
        List<Application> applications = applicationRepository.findAllWithApplicantByRecruitmentId(recruitmentId);
        return applications.stream()
                .map(ApplicationsResponse::from)
                .toList();
    }

    public ApplicationDetailsResponse findApplicationDetails(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
        List<NarrativeAnswer> narrativeAnswers = narrativeAnswerRepository.findAllByApplication(application);
        Map<Long, List<SelectiveAnswer>> selectiveAnswers = collectSelectiveAnswersByQuestion(
                selectiveAnswerRepository.findAllByApplication(application));
        return ApplicationDetailsResponse.of(application, narrativeAnswers, selectiveAnswers);
    }

    private Map<Long, List<SelectiveAnswer>> collectSelectiveAnswersByQuestion(List<SelectiveAnswer> selectiveAnswers) {
        return selectiveAnswers.stream()
                .collect(groupingBy(selectiveAnswer -> selectiveAnswer.getSelectiveQuestion().getId()));
    }

    @Transactional
    public void decideOutcome(EvaluationRequest request) {
        List<Application> applications = applicationRepository.findAllWithApplicantByRecruitmentId(request.recruitmentId());
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
