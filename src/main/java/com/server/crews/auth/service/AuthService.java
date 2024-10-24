package com.server.crews.auth.service;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.auth.repository.ApplicantRepository;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AdministratorRepository administratorRepository;
    private final ApplicantRepository applicantRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponse registerForAdmin(AdminLoginRequest request) {
        String clubName = request.clubName();
        String password = request.password();

        String encodedPassword = passwordEncoder.encode(password);
        Administrator administrator = new Administrator(clubName, encodedPassword);
        administratorRepository.save(administrator);
        String accessToken = jwtTokenProvider.createAccessToken(Role.ADMIN, clubName);
        return new TokenResponse(administrator.getClubName(), accessToken);
    }

    public TokenResponse loginForAdmin(AdminLoginRequest request) {
        String clubName = request.clubName();
        String password = request.password();

        Administrator administrator = administratorRepository.findByClubName(clubName)
                .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));
        validatePassword(password, administrator.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(Role.ADMIN, clubName);
        return new TokenResponse(administrator.getClubName(), accessToken);
    }

    @Transactional
    public TokenResponse registerForApplicant(ApplicantLoginRequest request) {
        String email = request.email();
        String password = request.password();

        String encodedPassword = passwordEncoder.encode(password);
        Applicant applicant = new Applicant(email, encodedPassword);
        applicantRepository.save(applicant);
        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, email);
        return new TokenResponse(applicant.getEmail(), accessToken);
    }

    public TokenResponse loginForApplicant(ApplicantLoginRequest request) {
        String email = request.email();
        String password = request.password();

        Applicant applicant = applicantRepository.findByEmail(email)
                .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));
        validatePassword(password, applicant.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, email);
        return new TokenResponse(applicant.getEmail(), accessToken);
    }

    private void validatePassword(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new CrewsException(CrewsErrorCode.WRONG_PASSWORD);
        }
    }

    public LoginUser findAdminAuthentication(String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        validateAdminAuthorization(accessToken);
        String clubName = jwtTokenProvider.getPayload(accessToken);
        Administrator administrator = administratorRepository.findByClubName(clubName)
                .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));
        return new LoginUser(administrator.getId(), Role.ADMIN);
    }

    private void validateAdminAuthorization(String accessToken) {
        Role role = jwtTokenProvider.getRole(accessToken);
        if (role != Role.ADMIN) {
            throw new CrewsException(CrewsErrorCode.UNAUTHORIZED_USER);
        }
    }

    public LoginUser findApplicantAuthentication(String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        validateApplicantAuthorization(accessToken);
        String email = jwtTokenProvider.getPayload(accessToken);
        Applicant applicant = applicantRepository.findByEmail(email)
                .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));
        return new LoginUser(applicant.getId(), Role.APPLICANT);
    }

    private void validateApplicantAuthorization(String accessToken) {
        Role role = jwtTokenProvider.getRole(accessToken);
        if (role != Role.APPLICANT) {
            throw new CrewsException(CrewsErrorCode.UNAUTHORIZED_USER);
        }
    }

    public LoginUser findAuthentication(String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        Role role = jwtTokenProvider.getRole(accessToken);
        String payload = jwtTokenProvider.getPayload(accessToken);
        if (role == Role.APPLICANT) {
            Applicant applicant = applicantRepository.findByEmail(payload)
                    .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));
            return new LoginUser(applicant.getId(), Role.APPLICANT);
        }
        Administrator administrator = administratorRepository.findByClubName(payload)
                .orElseThrow(() -> new CrewsException(CrewsErrorCode.USER_NOT_FOUND));
        return new LoginUser(administrator.getId(), Role.ADMIN);
    }
}
