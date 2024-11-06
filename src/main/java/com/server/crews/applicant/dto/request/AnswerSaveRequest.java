package com.server.crews.applicant.dto.request;

import com.server.crews.recruitment.controller.QuestionTypeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AnswerSaveRequest(
        @NotNull(message = "질문 id는 null일 수 없습니다.")
        Long questionId,
        @NotBlank(message = "질문 타입은 공백일 수 없습니다.")
        @QuestionTypeFormat
        String questionType,
        List<Long> choiceIds,
        String content
) {
}
