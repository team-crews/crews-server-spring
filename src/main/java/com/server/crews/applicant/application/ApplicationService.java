package com.server.crews.applicant.application;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicantAnswersResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
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
import com.server.crews.recruitment.repository.ChoiceRepository;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createApplication(Long applicantId, ApplicationSaveRequest request) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));

        Map<Long, AnswerSaveRequest> answerSaveRequestsByQuestionId = request.answers().stream()
                .collect(toMap(AnswerSaveRequest::questionId, Function.identity()));
        List<SelectiveAnswer> selectiveAnswers = toSelectiveAnswers(answerSaveRequestsByQuestionId);
        List<NarrativeAnswer> narrativeAnswers = toNarrativeAnswers(answerSaveRequestsByQuestionId);
        Application application = new Application(applicant, request.studentNumber(), request.major(), request.name(),
                narrativeAnswers, selectiveAnswers);

        applicationRepository.save(application);
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

    public List<ApplicationsResponse> findAllApplicants(Long recruitmentId) {
        List<Application> applications = applicationRepository.findAllByRecruitmentId(recruitmentId);
        return applications.stream()
                .map(ApplicationsResponse::from)
                .toList();
    }

    public ApplicantAnswersResponse findAllApplicantAnswers(Long applicantId) {
        validateApplicantId(applicantId);
        List<NarrativeAnswer> narrativeAnswers = narrativeAnswerRepository.findAllByApplicantId(applicantId);
        Map<Long, List<SelectiveAnswer>> selectiveAnswers = selectiveAnswerRepository.findAllByApplicantId(applicantId)
                .stream()
                .collect(groupingBy(selectiveAnswer -> selectiveAnswer.getSelectiveQuestion().getId()));
        return ApplicantAnswersResponse.of(narrativeAnswers, selectiveAnswers);
    }

    private void validateApplicantId(Long applicantId) {
        applicationRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    @Transactional
    public void decideOutcome(EvaluationRequest request, Long applicantId) {
        Application application = applicationRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
        application.decideOutcome(request.outcome());
    }

    @Transactional
    public void sendOutcomeEmail(Recruitment accessedRecruitment) {
        Long recruitmentId = accessedRecruitment.getId();
        List<Application> applications = applicationRepository.findAllByRecruitmentId(recruitmentId);

        applications.stream().filter(Application::isNotDetermined)
                .forEach(applicant -> applicant.decideOutcome(Outcome.FAIL));

        eventPublisher.publishEvent(new OutcomeDeterminedEvent(applications, accessedRecruitment));
    }
}
