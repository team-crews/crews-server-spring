package com.server.crews.auth.application;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.auth.repository.ApplicantRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AdministratorRepository administratorRepository;
    private final ApplicantRepository applicantRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AccessTokenResponse loginForAdmin(AdminLoginRequest request) {
        String clubName = request.clubName();
        String password = request.password();

        Administrator administrator = administratorRepository.findByClubName(clubName)
                .orElseGet(() -> createAdmin(clubName, password));
        String accessToken = jwtTokenProvider.createAccessToken(Role.ADMIN, clubName);
        return new AccessTokenResponse(administrator.getId(), accessToken);
    }

    private Administrator createAdmin(String clubName, String password) {
        Administrator administrator = new Administrator(clubName, password);
        return administratorRepository.save(administrator);
    }

    @Transactional
    public AccessTokenResponse loginForApplicant(ApplicantLoginRequest request) {
        String email = request.email();
        String password = request.password();
        Recruitment recruitment = recruitmentRepository.findByCode(request.recruitmentCode())
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));

        Applicant applicant = applicantRepository.findByEmailAndRecruitment(email, recruitment)
                .orElseGet(() -> createApplicant(email, password, recruitment));
        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, email);
        return new AccessTokenResponse(applicant.getId(), accessToken);
    }

    private Applicant createApplicant(String email, String password, Recruitment recruitment) {
        Applicant applicant = new Applicant(email, password, recruitment);
        return applicantRepository.save(applicant);
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
