package com.server.crews.applicant.dto.request;

import com.server.crews.recruitment.presentation.QuestionTypeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerSaveRequest(
        Long answerId,
        @NotBlank(message = "질문 타입은 공백일 수 없습니다.")
        @QuestionTypeFormat
        String questionType,
        @NotNull(message = "질문 id는 null일 수 없습니다.")
        Long questionId,
        String content,
        Long choiceId
) {
}
