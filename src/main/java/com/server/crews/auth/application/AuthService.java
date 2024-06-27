package com.server.crews.auth.application;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.dto.response.RefreshTokenDto;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.auth.domain.Role;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RecruitmentRepository recruitmentRepository;
    private final ApplicantRepository applicantRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final int refreshTokenValidityInSecond;

    public AuthService(JwtTokenProvider jwtTokenProvider, RecruitmentRepository recruitmentRepository,
                       ApplicantRepository applicantRepository, RefreshTokenRepository refreshTokenRepository,
                       @Value("${jwt.refresh-token-validity}") int refreshTokenValidityInMilliseconds) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.recruitmentRepository = recruitmentRepository;
        this.applicantRepository = applicantRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenValidityInSecond = refreshTokenValidityInMilliseconds / 1000;
    }

    @Transactional
    public RefreshTokenDto createRefreshToken(Role role, Long id) {
        String refreshToken = jwtTokenProvider.createRefreshToken(role, String.valueOf(id));
        refreshTokenRepository.deleteByOwnerId(id);
        refreshTokenRepository.save(new RefreshToken(refreshToken, id));
        return new RefreshTokenDto(refreshTokenValidityInSecond, refreshToken);
    }

    public Object findAuthentication(final String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        String payload = jwtTokenProvider.getPayload(accessToken);
        long id = Long.parseLong(payload);
        Role role = jwtTokenProvider.getRole(accessToken);
        if (role.equals(Role.ADMIN)) {
            return findExistingRecruitment(id);
        }
        return findExistingApplication(id);
    }

    private Recruitment findExistingRecruitment(final Long id) {
        return recruitmentRepository.findById(id)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
    }

    private Applicant findExistingApplication(final Long id) {
        return applicantRepository.findById(id)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    public AccessTokenResponse renew(final String refreshToken) {
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
