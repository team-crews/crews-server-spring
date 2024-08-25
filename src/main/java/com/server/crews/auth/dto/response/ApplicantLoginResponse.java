package com.server.crews.auth.dto.response;

import com.server.crews.recruitment.domain.RecruitmentProgress;

public record ApplicantLoginResponse(
        Long applicantId,
        String accessToken,
        RecruitmentProgress recruitmentProgress,
        Long applicationId
) {
}
