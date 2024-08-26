package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record ApplicationDetailsResponse(Long id, String studentNumber, String major, String name,
                                         List<SelectiveAnswerResponse> selectiveAnswers,
                                         List<NarrativeAnswerResponse> narrativeAnswers) {

    public static ApplicationDetailsResponse of(Application application, List<NarrativeAnswer> narrativeAnswers,
                                                Map<Long, List<SelectiveAnswer>> selectiveAnswers) {
        return ApplicationDetailsResponse.builder()
                .id(application.getId())
                .studentNumber(application.getStudentNumber())
                .major(application.getMajor())
                .name(application.getName())
                .narrativeAnswers(from(narrativeAnswers))
                .selectiveAnswers(from(selectiveAnswers))
                .build();
    }

    private static List<NarrativeAnswerResponse> from(List<NarrativeAnswer> narrativeAnswers) {
        return narrativeAnswers.stream()
                .map(NarrativeAnswerResponse::from)
                .toList();
    }

    private static List<SelectiveAnswerResponse> from(Map<Long, List<SelectiveAnswer>> selectiveAnswers) {
        return selectiveAnswers.entrySet()
                .stream()
                .map(entry -> SelectiveAnswerResponse.from(entry.getKey(), entry.getValue()))
                .toList();
    }
}
