package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class RecruitmentSaveRequest {
    private final String title;
    private final String clubName;
    private final String description;
    private final List<SectionsSaveRequest> sections;
    private final LocalDateTime deadline;

    public List<Section> createSections() {
        if (sections == null) {
            return List.of();
        }
        return sections.stream()
                .map(SectionsSaveRequest::toEntity)
                .toList();
    }
}
