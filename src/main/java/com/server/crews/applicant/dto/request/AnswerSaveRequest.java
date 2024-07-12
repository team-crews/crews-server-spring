package com.server.crews.applicant.dto.request;

import com.server.crews.recruitment.dto.request.QuestionType;

import java.util.List;

public record AnswerSaveRequest(QuestionType questionType, Long questionId, String content, List<Long> choiceIds) {
    public boolean isSelective() {
        return questionType == QuestionType.SELECTIVE;
    }

    public boolean isNarrative() {
        return questionType == QuestionType.NARRATIVE;
    }
}
