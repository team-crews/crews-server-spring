package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.SelectiveAnswer;
import java.util.List;

public record SelectiveAnswerResponse(Long questionId, List<SelectedChoiceResponse> choices) {

    public static SelectiveAnswerResponse from(Long questionId, List<SelectiveAnswer> selectiveAnswers) {
        return new SelectiveAnswerResponse(questionId, choiceIds(selectiveAnswers));
    }

    private static List<SelectedChoiceResponse> choiceIds(List<SelectiveAnswer> selectiveAnswers) {
        return selectiveAnswers.stream()
                .map(selectiveAnswer -> new SelectedChoiceResponse(selectiveAnswer.getId(),
                        selectiveAnswer.getChoice().getId()))
                .toList();
    }
}
