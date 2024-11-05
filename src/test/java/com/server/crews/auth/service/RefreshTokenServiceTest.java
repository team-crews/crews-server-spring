package com.server.crews.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.environ.service.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RefreshTokenServiceTest extends ServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("리프레시 토큰을 생성하고 저장한다.")
    void createRefreshToken() {
        // given
        Role role = Role.APPLICANT;
        String username = "mia";

        // when
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(role, username);

        // then
        assertThat(refreshToken.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("리프레시 토큰으로 액세스 토큰을 재발급 받는다.")
    void renew() {
        // given
        Role role = Role.APPLICANT;
        String username = "mia";
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(role, username);

        // when
        TokenResponse tokenResponse = refreshTokenService.renew(refreshToken.getToken());

        // then
        assertThat(tokenResponse.username()).isEqualTo(username);
    }
}
