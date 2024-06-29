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
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final SelectiveAnswerRepository selectiveAnswerRepository;
    private final NarrativeAnswerRepository narrativeAnswerRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createApplication(Long applicantId, ApplicationSaveRequest request) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));
        Application application = new Application(applicant, request.studentNumber(), request.major(), request.name());

        List<AnswerSaveRequest> answerSaveRequests = request.answers();
        Long applicationId = application.getId();
        List<SelectiveAnswer> selectiveAnswers = toSelectiveAnswers(answerSaveRequests, applicationId);
        List<NarrativeAnswer> narrativeAnswers = toNarrativeAnswers(answerSaveRequests, applicationId);

        applicationRepository.save(application);
        selectiveAnswerRepository.saveAll(selectiveAnswers);
        narrativeAnswerRepository.saveAll(narrativeAnswers);
    }

    private List<SelectiveAnswer> toSelectiveAnswers(List<AnswerSaveRequest> answerSaveRequests, Long applicantId) {
        List<AnswerSaveRequest> selectiveAnswerRequests = answerSaveRequests.stream()
                .filter(AnswerSaveRequest::isSelective)
                .toList();
        validateQuestionIds(selectiveAnswerRequests, selectiveQuestionRepository::existsAllByIdIn);
        return selectiveAnswerRequests.stream()
                .map(answerRequest -> answerRequest.toSelectiveAnswers(applicantId))
                .flatMap(List::stream)
                .toList();
    }

    private List<NarrativeAnswer> toNarrativeAnswers(List<AnswerSaveRequest> answerSaveRequests, Long applicantId) {
        List<AnswerSaveRequest> narrativeAnswerRequests = answerSaveRequests.stream()
                .filter(AnswerSaveRequest::isNarrative)
                .toList();
        validateQuestionIds(narrativeAnswerRequests, narrativeQuestionRepository::existsAllByIdIn);
        return narrativeAnswerRequests.stream()
                .map(answerRequest -> answerRequest.toNarrativeAnswer(applicantId))
                .toList();
    }

    private void validateQuestionIds(List<AnswerSaveRequest> answerSaveRequests, Function<List<Long>, Boolean> validationQuery) {
        List<Long> questionIds = answerSaveRequests.stream()
                .map(AnswerSaveRequest::questionId)
                .toList();
        boolean isValidIds = validationQuery.apply(questionIds);
        if (!answerSaveRequests.isEmpty() && !isValidIds) {
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
                .collect(groupingBy(SelectiveAnswer::getSelectiveQuestionId));
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
