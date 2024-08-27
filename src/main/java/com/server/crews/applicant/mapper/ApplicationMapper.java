package com.server.crews.applicant.mapper;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.response.AnswerResponse;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.QuestionType;
import java.util.ArrayList;
import java.util.List;

public class ApplicationMapper {

    public static ApplicationDetailsResponse applicationToApplicationDetailsResponse(Application application) {
        List<AnswerResponse> narrativeAnswerResponses = application.getNarrativeAnswers().stream()
                .map(AnswerMapper::narrativeAnswerToAnswerResponse)
                .toList();
        List<AnswerResponse> selectiveAnswerResponses = application.getSelectiveAnswers().stream()
                .map(AnswerMapper::selectiveAnswerToAnswerResponse)
                .toList();
        List<AnswerResponse> allAnswerResponses = new ArrayList<>(narrativeAnswerResponses);
        allAnswerResponses.addAll(selectiveAnswerResponses);
        return ApplicationDetailsResponse.builder()
                .id(application.getId())
                .studentNumber(application.getStudentNumber())
                .major(application.getMajor())
                .name(application.getName())
                .answers(allAnswerResponses)
                .build();
    }

    public static ApplicationsResponse applicationToApplicationsResponse(Application application) {
        return ApplicationsResponse.builder()
                .id(application.getId())
                .studentNumber(application.getStudentNumber())
                .name(application.getName())
                .major(application.getMajor())
                .outcome(application.getOutcome())
                .build();
    }

    public static Application applicationSaveRequestToApplication(ApplicationSaveRequest applicationSaveRequest,
                                                                  Recruitment recruitment, Applicant applicant) {
        List<AnswerSaveRequest> answerSaveRequests = applicationSaveRequest.answers();
        List<NarrativeAnswer> narrativeAnswers = answerSaveRequests.stream()
                .filter(answerSaveRequest -> QuestionType.NARRATIVE.hasSameName(answerSaveRequest.questionType()))
                .map(AnswerMapper::answerSaveRequestToNarrativeAnswer)
                .toList();
        List<SelectiveAnswer> selectiveAnswers = answerSaveRequests.stream()
                .filter(answerSaveRequest -> QuestionType.SELECTIVE.hasSameName(answerSaveRequest.questionType()))
                .map(AnswerMapper::answerSaveRequestToSelectiveAnswer)
                .toList();
        return new Application(
                applicationSaveRequest.id(),
                recruitment,
                applicant,
                applicationSaveRequest.studentNumber(),
                applicationSaveRequest.major(),
                applicationSaveRequest.name(),
                narrativeAnswers,
                selectiveAnswers);
    }
}
