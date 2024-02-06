package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.domain.Progress;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import java.util.List;

public record RecruitmentDetailsResponse(
        String title, String description, Progress progress, List<Section> sections) {
    public static RecruitmentDetailsResponse from(final Recruitment recruitment) {
        return new RecruitmentDetailsResponse(
                recruitment.getTitle(), recruitment.getDescription(),
                recruitment.getProgress(), recruitment.getSections());
    }
}
