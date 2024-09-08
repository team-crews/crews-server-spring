package com.server.crews.auth.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieGenerator {
    private static final String COOKIE_NAME = "refreshToken";
    private static final String COOKIE_PATH = "/auth/refresh";

    private final int defaultTokenValidity;

    public RefreshTokenCookieGenerator(@Value("${jwt.refresh-token-validity}") int refreshTokenValidityInMilliseconds) {
        this.defaultTokenValidity = refreshTokenValidityInMilliseconds;
    }

    public ResponseCookie generateWithDefaultValidity(String token) {
        return generate(this.defaultTokenValidity, token);
    }

    public ResponseCookie generate(long validity, String token) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path(COOKIE_PATH)
                .maxAge(validity)
                .sameSite("None")
                .build();
    }
}
