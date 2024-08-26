package com.server.crews.recruitment.mapper;

import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.response.ChoiceResponse;
import com.server.crews.recruitment.dto.response.QuestionResponse;
import java.util.List;

public class QuestionMapper {

    public static QuestionResponse narrativeQuestionToQuestionResponse(NarrativeQuestion narrativeQuestion) {
        return QuestionResponse.builder()
                .id(narrativeQuestion.getId())
                .type(QuestionType.NARRATIVE)
                .content(narrativeQuestion.getContent())
                .necessity(narrativeQuestion.getNecessity())
                .order(narrativeQuestion.getOrder())
                .wordLimit(narrativeQuestion.getWordLimit())
                .build();
    }

    public static QuestionResponse selectiveQuestionToQuestionResponse(SelectiveQuestion selectiveQuestion) {
        return QuestionResponse.builder()
                .id(selectiveQuestion.getId())
                .type(QuestionType.SELECTIVE)
                .content(selectiveQuestion.getContent())
                .necessity(selectiveQuestion.getNecessity())
                .order(selectiveQuestion.getOrder())
                .minimumSelection(selectiveQuestion.getMinimumSelection())
                .maximumSelection(selectiveQuestion.getMaximumSelection())
                .choices(choiceResponses(selectiveQuestion.getChoices()))
                .build();
    }

    private static List<ChoiceResponse> choiceResponses(List<Choice> choices) {
        return choices.stream()
                .map(choice -> new ChoiceResponse(choice.getId(), choice.getContent()))
                .toList();
    }
}
