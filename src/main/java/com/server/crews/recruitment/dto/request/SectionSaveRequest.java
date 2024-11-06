package com.server.crews.recruitment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SectionSaveRequest(
        Long id,
        @NotBlank(message = "섹션 이름은 공백일 수 없습니다.")
        String name,
        String description,
        @Valid
        @NotNull(message = "질문 리스트는 null일 수 없습니다. 빈 리스트를 보내주세요.")
        List<QuestionSaveRequest> questions
) {
}
