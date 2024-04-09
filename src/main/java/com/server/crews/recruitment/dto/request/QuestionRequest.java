package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestionRequest {
    private final QuestionType type;
    private final String content;
    private final Boolean necessity;
    private final Integer order; // unique
    private final Integer wordLimit;
    private final Integer minimumSelection;
    private final Integer maximumSelection;
    private final List<String> choices;

    public SelectiveQuestion createSelectiveQuestion() {
        return SelectiveQuestion.builder()
                .content(content)
                .necessity(necessity)
                .order(order)
                .minimumSelection(minimumSelection)
                .maximumSelection(maximumSelection)
                .choices(createChoices())
                .build();
    }

    private List<Choice> createChoices() {
        return choices.stream()
                .map(Choice::new)
                .toList();
    }

    public NarrativeQuestion createNarrativeQuestion() {
        return NarrativeQuestion.builder()
                .content(content)
                .necessity(necessity)
                .order(order)
                .wordLimit(wordLimit)
                .build();
    }

    public boolean isSelective() {
        return type == QuestionType.SELECTIVE;
    }

    public boolean isNarrative() {
        return type == QuestionType.NARRATIVE;
    }
}
