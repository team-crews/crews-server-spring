package com.server.crews.auth.application;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.Role;
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
public class LoginService {
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

    public Applicant createApplicant(String email, String password, Recruitment recruitment) {
        Applicant applicant = new Applicant(email, password, recruitment);
        return applicantRepository.save(applicant);
    }
}
