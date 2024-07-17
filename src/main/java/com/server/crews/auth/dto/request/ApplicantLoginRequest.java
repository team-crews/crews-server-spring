package com.server.crews.auth.dto.request;

public record ApplicantLoginRequest(
        String recruitmentCode,
        String email,
        String password
) {
}
