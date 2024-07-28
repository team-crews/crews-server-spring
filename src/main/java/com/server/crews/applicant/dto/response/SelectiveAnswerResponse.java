package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.recruitment.domain.Choice;

import java.util.List;

public record SelectiveAnswerResponse(Long questionId, List<Long> choiceIds) {

    public static SelectiveAnswerResponse from(Long questionId, List<SelectiveAnswer> selectiveAnswers) {
        return new SelectiveAnswerResponse(questionId, choiceIds(selectiveAnswers));
    }

    private static List<Long> choiceIds(List<SelectiveAnswer> selectiveAnswers) {
        return selectiveAnswers.stream()
                .map(SelectiveAnswer::getChoice)
                .map(Choice::getId)
                .toList();
    }
}
