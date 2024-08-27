package com.server.crews.applicant.dto.response;

import com.server.crews.recruitment.dto.request.QuestionType;
import lombok.Builder;

@Builder
public record AnswerResponse(
        Long answerId,
        Long questionId,
        String content,
        Long choiceId,
        QuestionType type
) {
}
