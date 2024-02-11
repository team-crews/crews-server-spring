package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.Section;
import java.time.LocalDateTime;
import java.util.List;

public record RecruitmentSaveRequest(
        String title, String clubName, String description, List<Section> sections, LocalDateTime deadline) {

}
