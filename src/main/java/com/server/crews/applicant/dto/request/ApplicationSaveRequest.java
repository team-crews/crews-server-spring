package com.server.crews.applicant.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ApplicationSaveRequest(
        Long id,
        @NotBlank(message = "학번은 공백일 수 없습니다.")
        String studentNumber,
        @NotBlank(message = "전공은 공백일 수 없습니다.")
        String major,
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        String name,
        List<AnswerSaveRequest> answers
) {
}
