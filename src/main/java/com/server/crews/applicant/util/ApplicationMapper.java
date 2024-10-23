package com.server.crews.applicant.util;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSectionSaveRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.applicant.dto.response.SectionAnswerResponse;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.QuestionType;
import java.util.Collection;
import java.util.List;

public class ApplicationMapper {

    public static ApplicationDetailsResponse applicationToApplicationDetailsResponse(Application application,
                                                                                     List<SectionAnswerResponse> sectionAnswerResponses) {
        return ApplicationDetailsResponse.builder()
                .id(application.getId())
                .studentNumber(application.getStudentNumber())
                .major(application.getMajor())
                .name(application.getName())
                .sections(sectionAnswerResponses)
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
                .map(ApplicationSectionSaveRequest::answers)
                .flatMap(Collection::stream)
                .filter(answerSaveRequest -> QuestionType.NARRATIVE.hasSameName(answerSaveRequest.questionType()))
                .filter(answerSaveRequest -> answerSaveRequest.content() != null)
                .map(AnswerMapper::answerSaveRequestToNarrativeAnswer)
                .toList();
    }

    public static List<SelectiveAnswer> selectiveAnswersInApplicationSaveRequest(
            ApplicationSaveRequest applicationSaveRequest) {
        return applicationSaveRequest.sections().stream()
                .map(ApplicationSectionSaveRequest::answers)
                .flatMap(Collection::stream)
                .filter(answerSaveRequest -> QuestionType.SELECTIVE.hasSameName(answerSaveRequest.questionType()))
                .filter(answerSaveRequest -> answerSaveRequest.choiceIds() != null)
                .map(AnswerMapper::answerSaveRequestToSelectiveAnswer)
                .flatMap(Collection::stream)
                .toList();
    }
}
