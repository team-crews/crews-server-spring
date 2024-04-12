package com.server.crews.fixture;

import com.server.crews.recruitment.domain.Section;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;

public class SectionFixture {
    public static final String BACKEND_SECTION_NAME = "BACKEND";
    public static final String FRONTEND_SECTION_NAME = "FRONTEND";

    public static Section SECTION(String name) {
        return Section.builder()
                .name(name)
                .description(DEFAULT_DESCRIPTION)
                .build();
    }
}
