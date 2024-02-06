package com.server.crews.auth.application;

import com.server.crews.application.domain.Application;
import com.server.crews.application.repository.ApplicationRepository;
import com.server.crews.auth.domain.Access;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.request.NewSecretCodeRequest;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RecruitmentRepository recruitmentRepository;
    private final ApplicationRepository applicationRepository;

    private final int refreshTokenValidityInSecond;

    public AuthService(
            final JwtTokenProvider jwtTokenProvider,
            final RecruitmentRepository recruitmentRepository,
            final ApplicationRepository applicationRepository,
            @Value("${jwt.refresh-token-validity}") final int refreshTokenValidityInMilliseconds) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.recruitmentRepository = recruitmentRepository;
        this.applicationRepository = applicationRepository;
        this.refreshTokenValidityInSecond = refreshTokenValidityInMilliseconds / 1000;
    }

    public TokenResponse createRecruitmentCode(final NewSecretCodeRequest request) {
        String code = request.code();
        validateDuplicatedRecruitmentCode(code);
        Recruitment recruitment = recruitmentRepository.save(new Recruitment(code));
        String id = recruitment.getId();

        String accessToken = jwtTokenProvider.createAccessToken(Role.ADMIN, id);
        return new TokenResponse(id, accessToken);
    }

    public ResponseCookie createRefreshToken(Role role, String id) {
        String refreshToken = jwtTokenProvider.createRefreshToken(role, id);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(refreshTokenValidityInSecond)
                .build();
        return responseCookie;
    }

    private void validateDuplicatedRecruitmentCode(final String code) {
        Recruitment existing = recruitmentRepository.findBySecretCode(code).orElse(null);
        validateDuplicated(existing);
    }

    public TokenResponse createApplicationCode(final NewSecretCodeRequest request) {
        String code = request.code();
        validateDuplicatedApplicationCode(code);
        Application application = applicationRepository.save(new Application(code));
        String id = application.getId();

        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, id);
        return new TokenResponse(id, accessToken);
    }

    private void validateDuplicatedApplicationCode(final String code) {
        Application existing = applicationRepository.findBySecretCode(code).orElse(null);
        validateDuplicated(existing);
    }

    private void validateDuplicated(Object existing) {
        if(Objects.nonNull(existing)) {
            throw new CrewsException(ErrorCode.DUPLICATE_SECRET_CODE);
        }
    }

    public Object createAuthentication(final String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        String id = jwtTokenProvider.getPayload(accessToken);
        Role role = jwtTokenProvider.getRole(accessToken);
        if(role.equals(Role.ADMIN)) {
            return validateExistingRecruitment(id);
        }
        return validateExistingApplication(id);
    }

    private Access validateExistingRecruitment(final String id) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        return new Access(recruitment.getId());
    }

    private Access validateExistingApplication(final String id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
        return new Access(application.getId());
    }
}
