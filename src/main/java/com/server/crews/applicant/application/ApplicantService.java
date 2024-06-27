package com.server.crews.applicant.application;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicantAnswersResponse;
import com.server.crews.applicant.dto.response.ApplicantsResponse;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.applicant.repository.NarrativeAnswerRepository;
import com.server.crews.applicant.repository.SelectiveAnswerRepository;
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
public class ApplicantService {
    private final ApplicantRepository applicantRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final SelectiveAnswerRepository selectiveAnswerRepository;
    private final NarrativeAnswerRepository narrativeAnswerRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void saveApplication(Applicant accessedApplicant, final ApplicationSaveRequest request) {
        accessedApplicant.updateAll(request);

        List<AnswerSaveRequest> answerSaveRequests = request.answers();
        Long applicantId = accessedApplicant.getId();
        List<SelectiveAnswer> selectiveAnswers = toSelectiveAnswers(answerSaveRequests, applicantId);
        List<NarrativeAnswer> narrativeAnswers = toNarrativeAnswers(answerSaveRequests, applicantId);

        applicantRepository.save(accessedApplicant);
        selectiveAnswerRepository.saveAll(selectiveAnswers);
        narrativeAnswerRepository.saveAll(narrativeAnswers);
    }

    private List<SelectiveAnswer> toSelectiveAnswers(final List<AnswerSaveRequest> answerSaveRequests, final Long applicantId) {
        List<AnswerSaveRequest> selectiveAnswerRequests = answerSaveRequests.stream()
                .filter(AnswerSaveRequest::isSelective)
                .toList();
        validateQuestionIds(selectiveAnswerRequests, selectiveQuestionRepository::existsAllByIdIn);
        return selectiveAnswerRequests.stream()
                .map(answerRequest -> answerRequest.toSelectiveAnswers(applicantId))
                .flatMap(List::stream)
                .toList();
    }

    private List<NarrativeAnswer> toNarrativeAnswers(final List<AnswerSaveRequest> answerSaveRequests, final Long applicantId) {
        List<AnswerSaveRequest> narrativeAnswerRequests = answerSaveRequests.stream()
                .filter(AnswerSaveRequest::isNarrative)
                .toList();
        validateQuestionIds(narrativeAnswerRequests, narrativeQuestionRepository::existsAllByIdIn);
        return narrativeAnswerRequests.stream()
                .map(answerRequest -> answerRequest.toNarrativeAnswer(applicantId))
                .toList();
    }

    private void validateQuestionIds(final List<AnswerSaveRequest> answerSaveRequests, final Function<List<Long>, Boolean> validationQuery) {
        List<Long> questionIds = answerSaveRequests.stream()
                .map(AnswerSaveRequest::questionId)
                .toList();
        boolean isValidIds = validationQuery.apply(questionIds);
        if (!answerSaveRequests.isEmpty() && !isValidIds) {
            throw new CrewsException(ErrorCode.QUESTION_NOT_FOUND);
        }
    }

    public List<ApplicantsResponse> findAllApplicants(final Long recruitmentId) {
        List<Applicant> applicants = applicantRepository.findAllByRecruitmentId(recruitmentId);
        return applicants.stream()
                .map(ApplicantsResponse::from)
                .toList();
    }

    public ApplicantAnswersResponse findAllApplicantAnswers(final Long applicantId) {
        validateApplicantId(applicantId);
        List<NarrativeAnswer> narrativeAnswers = narrativeAnswerRepository.findAllByApplicantId(applicantId);
        Map<Long, List<SelectiveAnswer>> selectiveAnswers = selectiveAnswerRepository.findAllByApplicantId(applicantId)
                .stream()
                .collect(groupingBy(SelectiveAnswer::getSelectiveQuestionId));
        return ApplicantAnswersResponse.of(narrativeAnswers, selectiveAnswers);
    }

    private void validateApplicantId(final Long applicantId) {
        applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    @Transactional
    public void decideOutcome(final EvaluationRequest request, final Long applicantId) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
        applicant.decideOutcome(request.outcome());
    }

    @Transactional
    public void sendOutcomeEmail(final Recruitment accessedRecruitment) {
        Long recruitmentId = accessedRecruitment.getId();
        List<Applicant> applicants = applicantRepository.findAllByRecruitmentId(recruitmentId);

        applicants.stream().filter(Applicant::isNotDetermined)
                .forEach(applicant -> applicant.decideOutcome(Outcome.FAIL));

        eventPublisher.publishEvent(new OutcomeDeterminedEvent(applicants, accessedRecruitment));
    }
}
