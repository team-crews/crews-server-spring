package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SectionRequest {
    private final String name;
    private final String description;
    private final List<QuestionRequest> questions;

    public Section toEntity() {
        return Section.builder()
                .name(name)
                .description(description)
                .narrativeQuestions(createNarrativeQuestions())
                .selectiveQuestions(createSelectiveQuestions())
                .build();
    }

    private List<NarrativeQuestion> createNarrativeQuestions() {
        return questions.stream()
                .filter(QuestionRequest::isNarrative)
                .map(QuestionRequest::createNarrativeQuestion)
                .toList();
    }

    private List<SelectiveQuestion> createSelectiveQuestions() {
        return questions.stream()
                .filter(QuestionRequest::isSelective)
                .map(QuestionRequest::createSelectiveQuestion)
                .toList();
    }
}
