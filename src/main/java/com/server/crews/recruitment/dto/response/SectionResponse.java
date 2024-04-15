package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import lombok.Builder;

import java.util.List;

@Builder
public record SectionResponse(
        String name, String description,
        List<NarrativeQuestionResponse> narrativeQuestions,
        List<SelectiveQuestionResponse> selectiveQuestions) {

    public static SectionResponse of(
            final Section section,
            final List<NarrativeQuestion> narrativeQuestions,
            final List<SelectiveQuestion> selectiveQuestions) {
        return SectionResponse.builder()
                .name(section.getName())
                .description(section.getDescription())
                .narrativeQuestions(narrativeQuestionResponses(narrativeQuestions))
                .selectiveQuestions(selectiveQuestionResponses(selectiveQuestions))
                .build();
    }

    private static List<NarrativeQuestionResponse> narrativeQuestionResponses(final List<NarrativeQuestion> narrativeQuestions) {
        return narrativeQuestions.stream()
                .map(NarrativeQuestionResponse::from)
                .toList();
    }

    private static List<SelectiveQuestionResponse> selectiveQuestionResponses(final List<SelectiveQuestion> selectiveQuestions) {
        return selectiveQuestions.stream()
                .map(SelectiveQuestionResponse::from)
                .toList();
    }
}
