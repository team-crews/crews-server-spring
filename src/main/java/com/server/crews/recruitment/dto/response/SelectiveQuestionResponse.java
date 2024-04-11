package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import lombok.Builder;

import java.util.List;

@Builder
public record SelectiveQuestionResponse(
        List<ChoiceResponse> choices, String content,
        Boolean necessity, Integer order,
        Integer minimumSelection, Integer maximumSelection) {

    public static SelectiveQuestionResponse from(final SelectiveQuestion question) {
        return SelectiveQuestionResponse.builder()
                .choices(choiceResponses(question.getChoices()))
                .content(question.getContent())
                .necessity(question.getNecessity())
                .order(question.getOrder())
                .minimumSelection(question.getMinimumSelection())
                .maximumSelection(question.getMaximumSelection())
                .build();
    }

    private static List<ChoiceResponse> choiceResponses(final List<Choice> choices) {
        return choices.stream()
                .map(ChoiceResponse::from)
                .toList();
    }
}