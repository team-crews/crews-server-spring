package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record QuestionSaveRequest(
        QuestionType type,
        @NotBlank(message = "질문 내용은 공백일 수 없습니다.")
        String content,
        @NotNull(message = "필수 항목 여부는 null일 수 없습니다.")
        Boolean necessity,
        @NotNull(message = "질문 순서는 null일 수 없습니다.")
        Integer order, // unique
        Integer wordLimit,
        Integer minimumSelection,
        Integer maximumSelection,
        List<String> choices
) {
    public SelectiveQuestion createSelectiveQuestion() {
        return new SelectiveQuestion(createsChoices(), content, necessity, order, minimumSelection, maximumSelection);
    }

    private List<Choice> createsChoices() {
        return choices.stream()
                .map(Choice::new)
                .toList();
    }

    public NarrativeQuestion createNarrativeQuestion() {
        return new NarrativeQuestion(content, necessity, order, wordLimit);
    }

    public boolean isSelective() {
        return type == QuestionType.SELECTIVE;
    }

    public boolean isNarrative() {
        return type == QuestionType.NARRATIVE;
    }
}
