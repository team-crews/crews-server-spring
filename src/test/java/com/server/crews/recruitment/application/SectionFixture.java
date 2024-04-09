package com.server.crews.recruitment.application;

import com.server.crews.recruitment.domain.Section;

import java.util.List;

import static com.server.crews.recruitment.application.QuestionFixture.NARRATIVE_QUESTION;
import static com.server.crews.recruitment.application.QuestionFixture.SELECTIVE_QUESTION;
import static com.server.crews.recruitment.application.RecruitmentFixture.DEFAULT_DESCRIPTION;

public class SectionFixture {
    public static final String BACKEND_SECTION_NAME = "BACKEND";
    public static final String FRONTEND_SECTION_NAME = "FRONTEND";

    public static final Section BACKEND_SECTION = Section.builder()
            .name(BACKEND_SECTION_NAME)
            .description(DEFAULT_DESCRIPTION)
            .narrativeQuestions(List.of(NARRATIVE_QUESTION))
            .selectiveQuestions(List.of(SELECTIVE_QUESTION))
            .build();
    public static final Section FRONTEND_SECTION = Section.builder()
            .name(FRONTEND_SECTION_NAME)
            .description(DEFAULT_DESCRIPTION)
            .narrativeQuestions(List.of(NARRATIVE_QUESTION))
            .selectiveQuestions(List.of(SELECTIVE_QUESTION))
            .build();
    public static final List<Section> DEV_SECTIONS = List.of(BACKEND_SECTION, FRONTEND_SECTION);
}
