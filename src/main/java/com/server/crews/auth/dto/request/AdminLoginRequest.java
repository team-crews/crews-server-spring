package com.server.crews.auth.dto.request;

public record AdminLoginRequest(
        String clubName,
        String password
) {
}
