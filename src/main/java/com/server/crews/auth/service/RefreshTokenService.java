package com.server.crews.auth.service;

import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.response.TokenResponse;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(Role role, String username);

    TokenResponse renew(String refreshToken);

    void delete(String username);
}
