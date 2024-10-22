package com.server.crews.applicant.dto.response;

import com.server.crews.recruitment.domain.QuestionType;
import java.util.List;
import lombok.Builder;

@Builder
public record AnswerResponse(
        Long questionId,
        String content,
        List<Long> choiceIds,
        QuestionType type
) {
}
