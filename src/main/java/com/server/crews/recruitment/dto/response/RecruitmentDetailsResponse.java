package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.domain.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
public record RecruitmentDetailsResponse(
        String title, String description, Progress progress, List<SectionResponse> sections, LocalDateTime deadline) {

    public static RecruitmentDetailsResponse from(
            final Recruitment recruitment,
            final Map<Section, List<NarrativeQuestion>> narrativeQuestionsBySection,
            final Map<Section, List<SelectiveQuestion>> selectiveQuestionsBySection) {
        return RecruitmentDetailsResponse.builder()
                .title(recruitment.getTitle())
                .description(recruitment.getDescription())
                .progress(recruitment.getProgress())
                .sections(sectionResponses(recruitment.getSections(), narrativeQuestionsBySection, selectiveQuestionsBySection))
                .deadline(recruitment.getDeadline())
                .build();
    }

    private static List<SectionResponse> sectionResponses(
            final List<Section> sections,
            final Map<Section, List<NarrativeQuestion>> narrativeQuestionsBySection,
            final Map<Section, List<SelectiveQuestion>> selectiveQuestionsBySection) {
        return sections.stream()
                .map(section -> SectionResponse.of(section, narrativeQuestionsBySection.getOrDefault(section, List.of()), selectiveQuestionsBySection.getOrDefault(section, List.of())))
                .toList();
    }
}
