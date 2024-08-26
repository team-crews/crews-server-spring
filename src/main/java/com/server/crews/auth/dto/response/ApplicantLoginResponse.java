package com.server.crews.auth.dto.response;

public record ApplicantLoginResponse(Long applicantId, String accessToken, Long applicationId) {
}
