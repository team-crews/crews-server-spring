package com.server.crews.recruitment.util;

import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.ChoiceSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.response.ChoiceResponse;
import com.server.crews.recruitment.dto.response.QuestionResponse;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionMapper {

    public static QuestionResponse narrativeQuestionToQuestionResponse(NarrativeQuestion narrativeQuestion) {
        return QuestionResponse.builder()
                .id(narrativeQuestion.getId())
                .type(QuestionType.NARRATIVE)
                .content(narrativeQuestion.getContent())
                .necessity(narrativeQuestion.getNecessity())
                .order(narrativeQuestion.getOrder())
                .wordLimit(narrativeQuestion.getWordLimit())
                .choices(List.of())
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
                .collect(Collectors.toList());
    }

    public static NarrativeQuestion questionSaveRequestToNarrativeQuestion(QuestionSaveRequest questionSaveRequest) {
        return new NarrativeQuestion(
                questionSaveRequest.id(),
                questionSaveRequest.content(),
                questionSaveRequest.necessity(),
                questionSaveRequest.order(),
                questionSaveRequest.wordLimit()
        );
    }

    public static SelectiveQuestion questionSaveRequestToSelectiveQuestion(QuestionSaveRequest questionSaveRequest) {
        return new SelectiveQuestion(
                questionSaveRequest.id(),
                choices(questionSaveRequest.choices()),
                questionSaveRequest.content(),
                questionSaveRequest.necessity(),
                questionSaveRequest.order(),
                questionSaveRequest.minimumSelection(),
                questionSaveRequest.maximumSelection());
    }

    private static List<Choice> choices(List<ChoiceSaveRequest> choiceSaveRequests) {
        return choiceSaveRequests.stream()
                .map(choiceSaveRequest -> new Choice(choiceSaveRequest.id(), choiceSaveRequest.content()))
                .toList();
    }
}
