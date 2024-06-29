package com.server.crews.recruitment.dto.request;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class RecruitmentSaveRequest {
    private final String title;
    private final String description;
    private final List<SectionsSaveRequest> sections;
    private final LocalDateTime closingDate;

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
