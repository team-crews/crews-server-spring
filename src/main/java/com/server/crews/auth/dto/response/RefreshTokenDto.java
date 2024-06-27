package com.server.crews.auth.dto.response;

import org.springframework.http.ResponseCookie;

public record RefreshTokenDto(long validity, String token) {

    public ResponseCookie toCookie() {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(validity)
                .build();
    }
}
