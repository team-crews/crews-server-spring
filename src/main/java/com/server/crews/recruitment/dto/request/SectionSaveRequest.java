package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SectionSaveRequest(
        Long id,
        @NotBlank(message = "섹션 이름은 공백일 수 없습니다.")
        String name,
        String description,
        @Valid
        List<QuestionSaveRequest> questions
) {

    public Section toEntity() {
        return new Section(id, name, description, narrativeQuestions(), selectiveQuestions());
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
