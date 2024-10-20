package com.server.crews.applicant.util;

import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.response.AnswerResponse;
import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.QuestionType;
import java.util.List;

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

    public static List<SelectiveAnswer> answerSaveRequestToSelectiveAnswer(AnswerSaveRequest answerSaveRequest) {
        return answerSaveRequest.choiceIds()
                .stream()
                .map(choiceId -> new SelectiveAnswer(new Choice(choiceId),
                        new SelectiveQuestion(answerSaveRequest.questionId())))
                .toList();
    }

    public static NarrativeAnswer answerSaveRequestToNarrativeAnswer(AnswerSaveRequest answerSaveRequest) {
        return new NarrativeAnswer(new NarrativeQuestion(answerSaveRequest.questionId()), answerSaveRequest.content());
    }
}
