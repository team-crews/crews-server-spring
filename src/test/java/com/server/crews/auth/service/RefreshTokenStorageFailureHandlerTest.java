package com.server.crews.auth.service;

import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.environ.service.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.RedisConnectionFailureException;

class RefreshTokenStorageFailureHandlerTest extends ServiceTest {

    @Autowired
    private RefreshTokenStorageFailureHandler refreshTokenStorageFailureHandler;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("리프레시 토큰을 생성할 때 저장소에 장애가 발생하면 만료시간이 0인 빈 토큰을 받는다.")
    void createRefreshToken() {
        // given
        BDDMockito.given(refreshTokenRepository.save(any()))
                .willThrow(RedisConnectionFailureException.class);

        // when
        RefreshToken refreshToken = refreshTokenStorageFailureHandler.createRefreshToken(Role.APPLICANT, TEST_EMAIL);

        // then
        assertAll(() -> {
            assertThat(refreshToken.getToken()).isEqualTo("");
            assertThat(refreshToken.getValidityInSeconds()).isEqualTo(0l);
        });
    }
}
