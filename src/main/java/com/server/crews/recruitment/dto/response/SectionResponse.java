package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import java.util.List;
import lombok.Builder;

@Builder
public record SectionResponse(
        Long id,
        String name,
        String description,
        List<NarrativeQuestionResponse> narrativeQuestions,
        List<SelectiveQuestionResponse> selectiveQuestions) {

    public static SectionResponse of(Section section) {
        return of(section, section.getNarrativeQuestions(), section.getSelectiveQuestions());
    }

    public static SectionResponse of(
            Section section, List<NarrativeQuestion> narrativeQuestions, List<SelectiveQuestion> selectiveQuestions) {
        return SectionResponse.builder()
                .id(section.getId())
                .name(section.getName())
                .description(section.getDescription())
                .narrativeQuestions(narrativeQuestionResponses(narrativeQuestions))
                .selectiveQuestions(selectiveQuestionResponses(selectiveQuestions))
                .build();
    }

    private static List<NarrativeQuestionResponse> narrativeQuestionResponses(
            List<NarrativeQuestion> narrativeQuestions) {
        return narrativeQuestions.stream()
                .map(NarrativeQuestionResponse::from)
                .toList();
    }

    private static List<SelectiveQuestionResponse> selectiveQuestionResponses(
            List<SelectiveQuestion> selectiveQuestions) {
        return selectiveQuestions.stream()
                .map(SelectiveQuestionResponse::from)
                .toList();
    }
}
