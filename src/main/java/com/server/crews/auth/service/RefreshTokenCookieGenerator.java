package com.server.crews.auth.service;

import com.server.crews.auth.domain.RefreshToken;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieGenerator {
    private static final String COOKIE_NAME = "refreshToken";
    private static final String COOKIE_PATH = "/auth/refresh";

    public ResponseCookie generate(RefreshToken refreshToken) {
        return generate(refreshToken.getValidityInSeconds(), refreshToken.getToken());
    }

    public ResponseCookie generate(long validity, String token) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path(COOKIE_PATH)
                .maxAge(validity)
                .sameSite("Strict")
                .build();
    }
}
