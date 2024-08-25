package com.server.crews.recruitment.dto.request;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.presentation.DateTimeFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record RecruitmentSaveRequest(
        Long id,
        @NotBlank(message = "모집공고 제목은 공백일 수 없습니다.")
        String title,
        String description,
        @Valid
        List<SectionSaveRequest> sections,
        @NotNull(message = "모집 마감 기한은 null일 수 없습니다.")
        @DateTimeFormat
        String deadline
) {
    public Recruitment toRecruitment(String code, Administrator publisher) {
        LocalDateTime deadlineDateTime = LocalDateTime.parse(deadline);
        return new Recruitment(id, code, title, description, deadlineDateTime, publisher, toSections());
    }

    public List<Section> toSections() {
        if (sections == null) {
            return List.of();
        }
        return sections.stream()
                .map(SectionSaveRequest::toEntity)
                .toList();
    }
}
