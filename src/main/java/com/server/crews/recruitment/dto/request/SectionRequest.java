package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SectionRequest {
    private final String name;
    private final String description;
    private final List<QuestionRequest> questions;

    public Section toEntity(Long recruitmentId) {
        return Section.builder()
                .recruitmentId(recruitmentId)
                .name(name)
                .description(description)
                .build();
    }
}
