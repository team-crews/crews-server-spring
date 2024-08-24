package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Progress;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record RecruitmentDetailsResponse(
        Long id, String title, String description, Progress progress, List<SectionResponse> sections,
        LocalDateTime deadline, String code) {

    public static RecruitmentDetailsResponse from(Recruitment recruitment) {
        return new RecruitmentDetailsResponse(recruitment.getId(), recruitment.getTitle(), recruitment.getDescription(),
                recruitment.getProgress(), sectionResponses(recruitment.getSections()), recruitment.getDeadline(),
                recruitment.getCode());
    }

    private static List<SectionResponse> sectionResponses(List<Section> sections) {
        return sections.stream()
                .map(SectionResponse::of)
                .toList();
    }

    public static RecruitmentDetailsResponse from(
            Recruitment recruitment, Map<Section, List<NarrativeQuestion>> narrativeQuestionsBySection,
            Map<Section, List<SelectiveQuestion>> selectiveQuestionsBySection) {
        List<SectionResponse> sectionResponses = sectionResponses(recruitment.getSections(),
                narrativeQuestionsBySection, selectiveQuestionsBySection);
        return new RecruitmentDetailsResponse(recruitment.getId(), recruitment.getTitle(), recruitment.getDescription(),
                recruitment.getProgress(), sectionResponses, recruitment.getDeadline(), recruitment.getCode());
    }

    private static List<SectionResponse> sectionResponses(
            List<Section> sections, Map<Section, List<NarrativeQuestion>> narrativeQuestionsBySection,
            Map<Section, List<SelectiveQuestion>> selectiveQuestionsBySection) {
        return sections.stream()
                .map(section -> SectionResponse.of(section, narrativeQuestionsBySection.getOrDefault(section, List.of()),
                        selectiveQuestionsBySection.getOrDefault(section, List.of())))
                .toList();
    }
}
