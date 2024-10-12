package com.server.crews.recruitment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record RecruitmentStateInProgressResponse(
        int applicationCount,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        LocalDateTime deadline,
        String code
) {
}
