package com.server.crews.auth.application;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.AdminLoginResponse;
import com.server.crews.auth.dto.response.ApplicantLoginResponse;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.auth.repository.ApplicantRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
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
    public AdminLoginResponse loginForAdmin(AdminLoginRequest request) {
        String clubName = request.clubName();
        String password = request.password();

        Administrator administrator = administratorRepository.findByClubName(clubName)
                .map(savedAdmin -> {
                    validatePassword(password, savedAdmin.getPassword());
                    return savedAdmin;
                })
                .orElseGet(() -> createAdmin(clubName, password));

        String accessToken = jwtTokenProvider.createAccessToken(Role.ADMIN, clubName);
        return new AdminLoginResponse(administrator.getClubName(), accessToken);
    }

    private Administrator createAdmin(String clubName, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        Administrator administrator = new Administrator(clubName, encodedPassword);
        return administratorRepository.save(administrator);
    }

    @Transactional
    public ApplicantLoginResponse loginForApplicant(ApplicantLoginRequest request) {
        String email = request.email();
        String password = request.password();

        Applicant applicant = applicantRepository.findByEmail(email)
                .map(savedApplicant -> {
                    validatePassword(password, savedApplicant.getPassword());
                    return savedApplicant;
                })
                .orElseGet(() -> createApplicant(email, password));

        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, email);
        return new ApplicantLoginResponse(applicant.getEmail(), accessToken);
    }

    private Applicant createApplicant(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        Applicant applicant = new Applicant(email, encodedPassword);
        return applicantRepository.save(applicant);
    }

    private void validatePassword(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new CrewsException(ErrorCode.WRONG_PASSWORD);
        }
    }

    public LoginUser findAdminAuthentication(String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        validateAdminAuthorization(accessToken);
        String clubName = jwtTokenProvider.getPayload(accessToken);
        Administrator administrator = administratorRepository.findByClubName(clubName)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));
        return new LoginUser(administrator.getId(), Role.ADMIN);
    }

    private void validateAdminAuthorization(String accessToken) {
        Role role = jwtTokenProvider.getRole(accessToken);
        if (role != Role.ADMIN) {
            throw new CrewsException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    public LoginUser findApplicantAuthentication(String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        validateApplicantAuthorization(accessToken);
        String email = jwtTokenProvider.getPayload(accessToken);
        Applicant applicant = applicantRepository.findByEmail(email)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));
        return new LoginUser(applicant.getId(), Role.APPLICANT);
    }

    private void validateApplicantAuthorization(String accessToken) {
        Role role = jwtTokenProvider.getRole(accessToken);
        if (role != Role.APPLICANT) {
            throw new CrewsException(ErrorCode.UNAUTHORIZED_USER);
        }
    }
}
