package com.server.crews.recruitment.dto.response;

import java.time.LocalDateTime;

public record RecruitmentStateInProgressResponse(int applicationCount, LocalDateTime deadline, String code) {
}
