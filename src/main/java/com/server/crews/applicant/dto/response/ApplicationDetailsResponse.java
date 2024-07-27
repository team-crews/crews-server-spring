package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.recruitment.domain.Choice;
import lombok.Builder;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Builder
public record ApplicationDetailsResponse(Long id, String studentNumber, String major, String name,
                                         Map<Long, List<Long>> choiceIdsBySelectiveQuestionId,
                                         Map<Long, String> answerByNarrativeQuestionId) {

    public static ApplicationDetailsResponse of(Application application, List<NarrativeAnswer> narrativeAnswers,
                                                Map<Long, List<SelectiveAnswer>> selectiveAnswers) {
        return ApplicationDetailsResponse.builder()
                .id(application.getId())
                .studentNumber(application.getStudentNumber())
                .major(application.getMajor())
                .name(application.getName())
                .answerByNarrativeQuestionId(from(narrativeAnswers))
                .choiceIdsBySelectiveQuestionId(from(selectiveAnswers))
                .build();
    }

    private static Map<Long, String> from(List<NarrativeAnswer> narrativeAnswers) {
        return narrativeAnswers.stream()
                .collect(toMap(NarrativeAnswer::getId, NarrativeAnswer::getContent));
    }

    private static Map<Long, List<Long>> from(Map<Long, List<SelectiveAnswer>> selectiveAnswers) {
        return selectiveAnswers.entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> choiceIds(entry.getValue())));
    }

    private static List<Long> choiceIds(List<SelectiveAnswer> selectiveAnswers) {
        return selectiveAnswers.stream()
                .map(SelectiveAnswer::getChoice)
                .map(Choice::getId)
                .toList();
    }
}
