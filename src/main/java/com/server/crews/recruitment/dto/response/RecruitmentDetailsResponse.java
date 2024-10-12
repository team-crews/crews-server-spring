package com.server.crews.recruitment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        LocalDateTime deadline,
        String code
) {
}
