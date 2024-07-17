package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;

import java.util.List;

public record SectionsSaveRequest(String name, String description, List<QuestionSaveRequest> questions) {
    public Section toEntity() {
        return new Section(name, description, narrativeQuestions(), selectiveQuestions());
    }

    private List<NarrativeQuestion> narrativeQuestions() {
        return questions.stream()
                .filter(QuestionSaveRequest::isNarrative)
                .map(QuestionSaveRequest::createNarrativeQuestion)
                .toList();
    }

    private List<SelectiveQuestion> selectiveQuestions() {
        return questions.stream()
                .filter(QuestionSaveRequest::isSelective)
                .map(QuestionSaveRequest::createSelectiveQuestion)
                .toList();
    }
}
