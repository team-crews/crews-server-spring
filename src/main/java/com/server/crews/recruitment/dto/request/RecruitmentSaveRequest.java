package com.server.crews.recruitment.dto.request;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;

import java.time.LocalDateTime;
import java.util.List;

public record RecruitmentSaveRequest(String title, String description, List<SectionsSaveRequest> sections,
                                     LocalDateTime closingDate) {
    public Recruitment toRecruitment(String code, Administrator publisher) {
        return new Recruitment(code, title, description, closingDate, publisher, toSections());
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
