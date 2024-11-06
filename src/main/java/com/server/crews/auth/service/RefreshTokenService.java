package com.server.crews.auth.service;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.auth.repository.ApplicantRepository;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.CrewsErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AdministratorRepository administratorRepository;
    private final ApplicantRepository applicantRepository;

    @Transactional
    public RefreshToken createRefreshToken(Role role, String username) {
        RefreshToken refreshToken = jwtTokenProvider.createRefreshToken(role, username);
        return refreshTokenRepository.save(refreshToken);
    }

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

    public void delete(Long userId, Role role) {
        if (role == Role.ADMIN) {
            Administrator administrator = administratorRepository.findById(userId)
                    .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));
            refreshTokenRepository.deleteById(administrator.getClubName());
            return;
        }
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteById(applicant.getEmail());
    }
}
