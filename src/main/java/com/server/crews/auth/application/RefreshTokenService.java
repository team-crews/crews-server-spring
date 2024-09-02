package com.server.crews.auth.application;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.RefreshTokenWithValidity;
import com.server.crews.auth.dto.response.TokenRefreshResponse;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.auth.repository.ApplicantRepository;
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
    private final AdministratorRepository administratorRepository;
    private final ApplicantRepository applicantRepository;

    private final int refreshTokenValidityInSecond;

    public RefreshTokenService(JwtTokenProvider jwtTokenProvider,
                               RefreshTokenRepository refreshTokenRepository,
                               AdministratorRepository administratorRepository,
                               ApplicantRepository applicantRepository,
                               @Value("${jwt.refresh-token-validity}") int refreshTokenValidityInMilliseconds) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.administratorRepository = administratorRepository;
        this.applicantRepository = applicantRepository;
        this.refreshTokenValidityInSecond = refreshTokenValidityInMilliseconds / 1000;
    }

    @Transactional
    public RefreshTokenWithValidity createRefreshToken(Role role, String username) {
        String refreshToken = jwtTokenProvider.createRefreshToken(role, username);
        refreshTokenRepository.deleteByUsername(username);
        refreshTokenRepository.save(new RefreshToken(username, refreshToken));
        return new RefreshTokenWithValidity(refreshTokenValidityInSecond, refreshToken);
    }

    public TokenRefreshResponse renew(String refreshToken) {
        jwtTokenProvider.validateRefreshToken(refreshToken);
        String username = jwtTokenProvider.getPayload(refreshToken);
        RefreshToken savedRefreshToken = refreshTokenRepository.findByUsername(username)
                .orElseThrow(() -> new CrewsException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        if (!savedRefreshToken.isSameToken(refreshToken)) {
            throw new CrewsException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Role role = jwtTokenProvider.getRole(refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(role, username);
        return new TokenRefreshResponse(accessToken);
    }

    public void delete(Long userId, Role role) {
        if (role == Role.ADMIN) {
            Administrator administrator = administratorRepository.findById(userId)
                    .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));
            refreshTokenRepository.deleteByUsername(administrator.getClubName());
            return;
        }
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUsername(applicant.getEmail());
    }
}
