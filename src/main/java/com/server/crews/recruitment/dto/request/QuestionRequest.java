package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Question;
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

    public Question toEntity(final Long sectionId) {
        if (isSelective()) {
            return SelectiveQuestion.builder()
                    .sectionId(sectionId)
                    .content(content)
                    .necessity(necessity)
                    .order(order)
                    .minimumSelection(minimumSelection)
                    .maximumSelection(maximumSelection)
                    .build();
        }
        return NarrativeQuestion.builder()
                .sectionId(sectionId)
                .content(content)
                .necessity(necessity)
                .order(order)
                .wordLimit(wordLimit)
                .build();
    }

    public boolean isSelective() {
        return type == QuestionType.SELECTIVE;
    }
}
