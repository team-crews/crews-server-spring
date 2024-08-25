package com.server.crews.auth.dto.response;

import com.server.crews.recruitment.domain.RecruitmentProgress;

public record AdminLoginResponse(
        Long adminId,
        String accessToken,
        RecruitmentProgress recruitmentProgress,
        Long recruitmentId
) {
}
