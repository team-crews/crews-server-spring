package com.server.crews.auth.application;

import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.RefreshTokenWithValidity;
import com.server.crews.auth.dto.response.LoginResponse;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    private final int refreshTokenValidityInSecond;

    public RefreshTokenService(JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository,
                               @Value("${jwt.refresh-token-validity}") int refreshTokenValidityInMilliseconds) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenValidityInSecond = refreshTokenValidityInMilliseconds / 1000;
    }

    @Transactional
    public RefreshTokenWithValidity createRefreshToken(Role role, Long id) {
        String refreshToken = jwtTokenProvider.createRefreshToken(role, String.valueOf(id));
        refreshTokenRepository.deleteByOwnerId(id);
        refreshTokenRepository.save(new RefreshToken(refreshToken, id));
        return new RefreshTokenWithValidity(refreshTokenValidityInSecond, refreshToken);
    }

    public LoginResponse renew(String refreshToken) {
        jwtTokenProvider.validateRefreshToken(refreshToken);
        refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CrewsException(ErrorCode.INVALID_REFRESH_TOKEN));

        String payload = jwtTokenProvider.getPayload(refreshToken);
        long id = Long.parseLong(payload);
        Role role = jwtTokenProvider.getRole(refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(role, payload);
        return new LoginResponse(id, accessToken, null);
    }
}
