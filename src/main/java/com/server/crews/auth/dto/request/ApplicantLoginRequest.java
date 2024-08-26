package com.server.crews.auth.dto.request;

public record ApplicantLoginRequest(
        String email,
        String password
) {
}
