package com.server.crews.recruitment.application;

import com.server.crews.recruitment.domain.Section;

import java.util.List;

import static com.server.crews.recruitment.application.RecruitmentFixture.DEFAULT_DESCRIPTION;

public class SectionFixture {
    public static final String BACKEND_SECTION_NAME = "BACKEND";
    public static final String FRONTEND_SECTION_NAME = "FRONTEND";

    public static Section SECTION(String name) {
        return Section.builder()
                .name(BACKEND_SECTION_NAME)
                .description(DEFAULT_DESCRIPTION)
                .build();
    }

    public static List<Section> DEV_SECTIONS() {
        return List.of(SECTION(BACKEND_SECTION_NAME), SECTION(FRONTEND_SECTION_NAME));
    }
}
