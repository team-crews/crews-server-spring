package com.server.crews.applicant.util;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.SectionSaveRequest;
import com.server.crews.applicant.dto.response.AnswerResponse;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.QuestionType;
import java.util.ArrayList;
import java.util.Collection;
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
                                                                  Recruitment recruitment, Long applicantId,
                                                                  List<NarrativeAnswer> narrativeAnswers,
                                                                  List<SelectiveAnswer> selectiveAnswers) {
        return new Application(
                applicationSaveRequest.id(),
                recruitment,
                applicantId,
                applicationSaveRequest.studentNumber(),
                applicationSaveRequest.major(),
                applicationSaveRequest.name(),
                narrativeAnswers,
                selectiveAnswers);
    }

    public static List<NarrativeAnswer> narrativeAnswersInApplicationSaveRequest(
            ApplicationSaveRequest applicationSaveRequest) {
        return applicationSaveRequest.sections().stream()
                .map(SectionSaveRequest::answers)
                .flatMap(Collection::stream)
                .filter(answerSaveRequest -> QuestionType.NARRATIVE.hasSameName(answerSaveRequest.questionType()))
                .map(AnswerMapper::answerSaveRequestToNarrativeAnswer)
                .toList();
    }

    public static List<SelectiveAnswer> selectiveAnswersInApplicationSaveRequest(
            ApplicationSaveRequest applicationSaveRequest) {
        return applicationSaveRequest.sections().stream()
                .map(SectionSaveRequest::answers)
                .flatMap(Collection::stream)
                .filter(answerSaveRequest -> QuestionType.SELECTIVE.hasSameName(answerSaveRequest.questionType()))
                .map(AnswerMapper::answerSaveRequestToSelectiveAnswer)
                .flatMap(Collection::stream)
                .toList();
    }
}
