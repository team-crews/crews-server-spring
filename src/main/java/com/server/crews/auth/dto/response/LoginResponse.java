package com.server.crews.auth.dto.response;

import com.server.crews.recruitment.domain.Progress;

public record LoginResponse(Long userId, String accessToken, Progress progress) {
}
