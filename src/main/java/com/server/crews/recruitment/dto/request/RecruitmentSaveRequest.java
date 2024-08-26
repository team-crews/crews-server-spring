package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.presentation.DateTimeFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RecruitmentSaveRequest(
        Long id,
        @NotBlank(message = "모집공고 제목은 공백일 수 없습니다.")
        String title,
        String description,
        @Valid
        @NotNull(message = "섹션 리스트는 null일 수 없습니다. 빈 리스트를 보내주세요.")
        List<SectionSaveRequest> sections,
        @NotNull(message = "모집 마감 기한은 null일 수 없습니다.")
        @DateTimeFormat
        String deadline
) {
}
