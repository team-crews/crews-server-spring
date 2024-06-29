package com.server.crews.auth.application;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.dto.RefreshTokenWithValidity;
import com.server.crews.auth.dto.response.AccessTokenResponse;
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
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AdministratorRepository administratorRepository;
    private final ApplicantRepository applicantRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final int refreshTokenValidityInSecond;

    public AuthService(JwtTokenProvider jwtTokenProvider, AdministratorRepository administratorRepository,
                       ApplicantRepository applicantRepository, RefreshTokenRepository refreshTokenRepository,
                       @Value("${jwt.refresh-token-validity}") int refreshTokenValidityInMilliseconds) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.administratorRepository = administratorRepository;
        this.applicantRepository = applicantRepository;
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

    public LoginUser findAuthentication(String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        String payload = jwtTokenProvider.getPayload(accessToken);
        Role role = jwtTokenProvider.getRole(accessToken);
        if (role == Role.ADMIN) {
            Administrator administrator = administratorRepository.findByClubName(payload)
                    .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));
            return new LoginUser(administrator.getId(), Role.ADMIN);
        }
        Applicant applicant = applicantRepository.findByEmail(payload)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));
        return new LoginUser(applicant.getId(), Role.APPLICANT);
    }

    public AccessTokenResponse renew(String refreshToken) {
        jwtTokenProvider.validateRefreshToken(refreshToken);
        refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CrewsException(ErrorCode.INVALID_REFRESH_TOKEN));

        String payload = jwtTokenProvider.getPayload(refreshToken);
        long id = Long.parseLong(payload);
        Role role = jwtTokenProvider.getRole(refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(role, payload);
        return new AccessTokenResponse(id, accessToken);
    }
}
