package com.server.crews.auth.service;

import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenManager implements RefreshTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken createRefreshToken(Role role, String username) {
        RefreshToken refreshToken = jwtTokenProvider.createRefreshToken(role, username);
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public TokenResponse renew(String refreshToken) {
        jwtTokenProvider.validateRefreshToken(refreshToken);
        String username = jwtTokenProvider.getPayload(refreshToken);
        RefreshToken savedRefreshToken = refreshTokenRepository.findById(username)
                .orElseThrow(() -> new CrewsException(CrewsErrorCode.REFRESH_TOKEN_NOT_FOUND));
        if (!savedRefreshToken.isSameToken(refreshToken)) {
            throw new CrewsException(CrewsErrorCode.INVALID_REFRESH_TOKEN);
        }

        Role role = jwtTokenProvider.getRole(refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(role, username);
        return new TokenResponse(username, accessToken);
    }

    @Override
    public void delete(String username) {
        refreshTokenRepository.deleteById(username);
    }
}
