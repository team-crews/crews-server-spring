package com.server.crews.applicant.mapper;

import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.response.AnswerResponse;
import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.domain.QuestionType;
import java.util.List;

public class AnswerMapper {

    public static AnswerResponse selectiveAnswerToAnswerResponse(List<SelectiveAnswer> selectiveAnswers) {
        List<Long> choiceIds = selectiveAnswers.stream()
                .map(SelectiveAnswer::getChoiceId)
                .toList();
        Long questionId = selectiveAnswers.get(0).getQuestionId();
        return AnswerResponse.builder()
                .questionId(questionId)
                .choiceIds(choiceIds)
                .type(QuestionType.SELECTIVE)
                .build();
    }

    public static AnswerResponse narrativeAnswerToAnswerResponse(NarrativeAnswer narrativeAnswer) {
        return AnswerResponse.builder()
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
