package com.server.crews.applicant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ApplicationSaveRequest(
        Long id,
        @NotBlank(message = "학번은 공백일 수 없습니다.")
        String studentNumber,
        @NotBlank(message = "전공은 공백일 수 없습니다.")
        String major,
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        String name,
        @Valid
        @NotNull(message = "섹션 답변 리스트는 null일 수 없습니다.")
        List<SectionSaveRequest> sections,
        @NotBlank(message = "모집 공고 코드는 공백일 수 없습니다.")
        String recruitmentCode
) {
}
