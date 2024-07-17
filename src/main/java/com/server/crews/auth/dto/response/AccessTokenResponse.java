package com.server.crews.auth.dto.response;

public record AccessTokenResponse(Long userId, String accessToken) {
}
