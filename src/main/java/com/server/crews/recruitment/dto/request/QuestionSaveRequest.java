package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.presentation.QuestionTypeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record QuestionSaveRequest(
        Long id,
        @NotBlank(message = "질문 타입은 공백일 수 없습니다.")
        @QuestionTypeFormat
        String type,
        @NotBlank(message = "질문 내용은 공백일 수 없습니다.")
        String content,
        @NotNull(message = "필수 항목 여부는 null일 수 없습니다.")
        Boolean necessity,
        @NotNull(message = "질문 순서는 null일 수 없습니다.")
        Integer order, // unique
        Integer wordLimit,
        Integer minimumSelection,
        Integer maximumSelection,
        @NotNull(message = "선택지 리스트는 null일 수 없습니다. 빈 리스트를 보내주세요.")
        List<ChoiceSaveRequest> choices
) {
}
