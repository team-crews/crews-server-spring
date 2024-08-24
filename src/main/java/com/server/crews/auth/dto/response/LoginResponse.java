package com.server.crews.auth.dto.response;

import com.server.crews.recruitment.domain.RecruitmentProgress;

public record LoginResponse(Long userId, String accessToken, RecruitmentProgress recruitmentProgress) {
}
