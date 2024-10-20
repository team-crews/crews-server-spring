package com.server.crews.applicant.util;

import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.response.AnswerResponse;
import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.QuestionType;

public class AnswerMapper {

    public static AnswerResponse selectiveAnswerToAnswerResponse(SelectiveAnswer selectiveAnswer) {
        return AnswerResponse.builder()
                .answerId(selectiveAnswer.getId())
                .questionId(selectiveAnswer.getSelectiveQuestion().getId())
                .choiceId(selectiveAnswer.getChoice().getId())
                .type(QuestionType.SELECTIVE)
                .build();
    }

    public static AnswerResponse narrativeAnswerToAnswerResponse(NarrativeAnswer narrativeAnswer) {
        return AnswerResponse.builder()
                .answerId(narrativeAnswer.getId())
                .questionId(narrativeAnswer.getNarrativeQuestion().getId())
                .content(narrativeAnswer.getContent())
                .type(QuestionType.NARRATIVE)
                .build();
    }

    public static SelectiveAnswer answerSaveRequestToSelectiveAnswer(AnswerSaveRequest answerSaveRequest) {
        return new SelectiveAnswer(
                answerSaveRequest.answerId(),
                new Choice(answerSaveRequest.choiceId()),
                new SelectiveQuestion(answerSaveRequest.questionId()));
    }

    public static NarrativeAnswer answerSaveRequestToNarrativeAnswer(AnswerSaveRequest answerSaveRequest) {
        return new NarrativeAnswer(answerSaveRequest.answerId(),
                new NarrativeQuestion(answerSaveRequest.questionId()),
                answerSaveRequest.content());
    }
}
