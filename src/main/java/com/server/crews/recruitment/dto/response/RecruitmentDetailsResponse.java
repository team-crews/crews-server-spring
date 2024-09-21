package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.domain.RecruitmentProgress;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RecruitmentDetailsResponse(
        Long id,
        String title,
        String description,
        RecruitmentProgress recruitmentProgress,
        List<SectionResponse> sections,
        LocalDateTime deadline,
        String code
) {
}
