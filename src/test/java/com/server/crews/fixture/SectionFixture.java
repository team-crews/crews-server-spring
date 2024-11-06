package com.server.crews.fixture;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;

import com.server.crews.recruitment.domain.Section;
import java.util.List;

public class SectionFixture {
    public static final String BACKEND_SECTION_NAME = "BACKEND";
    public static final String FRONTEND_SECTION_NAME = "FRONTEND";

    public static Section SECTION(String name) {
        return new Section(null, name, DEFAULT_DESCRIPTION, List.of(), List.of());
    }
}
