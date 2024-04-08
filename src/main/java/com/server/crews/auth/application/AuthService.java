package com.server.crews.auth.application;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.request.LoginRequest;
import com.server.crews.auth.dto.request.NewSecretCodeRequest;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
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

    public AuthService(
            final JwtTokenProvider jwtTokenProvider,
            final RecruitmentRepository recruitmentRepository,
            final ApplicantRepository applicantRepository,
            final RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refresh-token-validity}") final int refreshTokenValidityInMilliseconds) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.recruitmentRepository = recruitmentRepository;
        this.applicantRepository = applicantRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenValidityInSecond = refreshTokenValidityInMilliseconds / 1000;
    }

    @Transactional
    public TokenResponse createRecruitmentCode(final NewSecretCodeRequest request) {
        String code = request.code();
        validateDuplicatedRecruitmentCode(code);
        Recruitment recruitment = recruitmentRepository.save(new Recruitment(code));
        String id = recruitment.getId();

        String accessToken = jwtTokenProvider.createAccessToken(Role.ADMIN, id);
        return new TokenResponse(id, accessToken);
    }

    private void validateDuplicatedRecruitmentCode(final String code) {
        recruitmentRepository.findBySecretCode(code)
                .orElseThrow(() -> new CrewsException(ErrorCode.DUPLICATE_SECRET_CODE));
    }

    @Transactional
    public ResponseCookie createRefreshToken(Role role, String id) {
        String refreshToken = jwtTokenProvider.createRefreshToken(role, id);
        refreshTokenRepository.deleteByOwnerId(id);
        refreshTokenRepository.save(new RefreshToken(refreshToken, id));

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(refreshTokenValidityInSecond)
                .build();
    }

    @Transactional
    public TokenResponse createApplicationCode(final NewSecretCodeRequest request) {
        String code = request.code();
        validateDuplicatedApplicationCode(code);
        Applicant applicant = applicantRepository.save(new Applicant(code));
        String id = applicant.getId();

        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, id);
        return new TokenResponse(id, accessToken);
    }

    private void validateDuplicatedApplicationCode(final String code) {
        applicantRepository.findBySecretCode(code)
                .orElseThrow(() -> new CrewsException(ErrorCode.DUPLICATE_SECRET_CODE));
    }

    public Object findAuthentication(final String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        String id = jwtTokenProvider.getPayload(accessToken);
        Role role = jwtTokenProvider.getRole(accessToken);
        if (role.equals(Role.ADMIN)) {
            return findExistingRecruitment(id);
        }
        return findExistingApplication(id);
    }

    private Recruitment findExistingRecruitment(final String id) {
        return recruitmentRepository.findById(id)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
    }

    private Applicant findExistingApplication(final String id) {
        return applicantRepository.findById(id)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    public TokenResponse loginForAdmin(final LoginRequest request) {
        Recruitment recruitment = recruitmentRepository.findBySecretCode(request.code())
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));

        String id = recruitment.getId();
        String accessToken = jwtTokenProvider.createAccessToken(Role.ADMIN, id);
        return new TokenResponse(id, accessToken);
    }

    public TokenResponse loginForApplicant(final LoginRequest request) {
        Applicant applicant = applicantRepository.findBySecretCode(request.code())
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));

        String id = applicant.getId();
        String accessToken = jwtTokenProvider.createAccessToken(Role.ADMIN, id);
        return new TokenResponse(id, accessToken);
    }

    public TokenResponse renew(final String refreshToken) {
        jwtTokenProvider.validateRefreshToken(refreshToken);
        refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CrewsException(ErrorCode.INVALID_REFRESH_TOKEN));

        String id = jwtTokenProvider.getPayload(refreshToken);
        Role role = jwtTokenProvider.getRole(refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(role, id);
        return new TokenResponse(id, accessToken);
    }
}
