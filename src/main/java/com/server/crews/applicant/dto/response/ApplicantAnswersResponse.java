package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import lombok.Builder;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Builder
public record ApplicantAnswersResponse(Map<Long, List<Long>> choiceIdsBySelectiveQuestionId,
                                       Map<Long, String> answerByNarrativeQuestionId) {

    public static ApplicantAnswersResponse of(
            final List<NarrativeAnswer> narrativeAnswers,
            final Map<Long, List<SelectiveAnswer>> selectiveAnswers) {
        return ApplicantAnswersResponse.builder()
                .answerByNarrativeQuestionId(from(narrativeAnswers))
                .choiceIdsBySelectiveQuestionId(from(selectiveAnswers))
                .build();
    }

    private static Map<Long, String> from(final List<NarrativeAnswer> narrativeAnswers) {
        return narrativeAnswers.stream()
                .collect(toMap(NarrativeAnswer::getId, NarrativeAnswer::getContent));
    }

    private static Map<Long, List<Long>> from(final Map<Long, List<SelectiveAnswer>> selectiveAnswers) {
        return selectiveAnswers.entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> choiceIds(entry.getValue())));
    }

    private static List<Long> choiceIds(final List<SelectiveAnswer> selectiveAnswers) {
        return selectiveAnswers.stream()
                .map(SelectiveAnswer::getChoiceId)
                .toList();
    }
}
