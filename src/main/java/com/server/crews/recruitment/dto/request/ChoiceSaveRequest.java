package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.Choice;
import jakarta.validation.constraints.NotBlank;

public record ChoiceSaveRequest(
        Long id,
        @NotBlank(message = "선택지 내용은 공백일 수 없습니다.")
        String content
) {

    public Choice toEntity() {
        return new Choice(id, content);
    }
}
