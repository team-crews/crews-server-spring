package com.server.crews.recruitment.dto.request;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.recruitment.presentation.DateTimeFormat;
import com.server.crews.recruitment.presentation.DateTimeFormatValidator;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record RecruitmentSaveRequest(
        @NotBlank(message = "모집공고 제목은 공백일 수 없습니다.")
        String title,
        String description,
        @Valid
        List<SectionsSaveRequest> sections,
        @NotNull(message = "모집 마감일은 null일 수 없습니다.")
        @DateTimeFormat
        String closingDate
) {
    public Recruitment toRecruitment(String code, Administrator publisher) {
        LocalDateTime closingDateTime = LocalDateTime.parse(closingDate, DateTimeFormatValidator.DATE_TIME_FORMATTER);
        return new Recruitment(code, title, description, closingDateTime, publisher, toSections());
    }

    public List<Section> toSections() {
        if (sections == null) {
            return List.of();
        }
        return sections.stream()
                .map(SectionsSaveRequest::toEntity)
                .toList();
    }
}
