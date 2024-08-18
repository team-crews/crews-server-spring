package com.server.crews.auth.application;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.LoginResponse;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.auth.repository.ApplicantRepository;
import com.server.crews.environ.service.ServiceTest;
import com.server.crews.recruitment.domain.Recruitment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class AuthServiceTest extends ServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("동아리 관리자가 로그인한다.")
    void loginNotSignedUpAdmin() {
        // given
        String clubName = "멋쟁이사자처럼";
        String password = "new password";
        AdminLoginRequest request = new AdminLoginRequest(clubName, password);

        // when
        LoginResponse loginResponse = authService.loginForAdmin(request);

        // then
        Optional<Administrator> createdAdmin = administratorRepository.findById(loginResponse.userId());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdAdmin).isNotEmpty();
            softAssertions.assertThat(loginResponse.accessToken()).isNotNull();
        });
    }

    @Test
    @DisplayName("가입된 관리자가 로그인 요청을 하면 액세스 토큰을 발급한다.")
    void loginAdmin() {
        // given
        Administrator administrator = LIKE_LION_ADMIN().administrator();
        AdminLoginRequest request = new AdminLoginRequest(administrator.getClubName(), administrator.getPassword());

        // when
        LoginResponse loginResponse = authService.loginForAdmin(request);

        // then
        Optional<Administrator> createdAdmin = administratorRepository.findById(loginResponse.userId());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdAdmin).isNotEmpty();
            softAssertions.assertThat(loginResponse.accessToken()).isNotNull();
        });
    }

    @Test
    @DisplayName("가입되지 않은 지원자가 로그인 요청을 하면 계정을 생성하고 액세스 토큰을 발급한다.")
    void loginNotSignedUpApplicant() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher).recruitment();
        String email = "new@gamil.com";
        String password = "new password";
        ApplicantLoginRequest request = new ApplicantLoginRequest(recruitment.getCode(), email, password);

        // when
        LoginResponse loginResponse = authService.loginForApplicant(request);

        // then
        Optional<Applicant> createdApplicant = applicantRepository.findById(loginResponse.userId());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdApplicant).isNotEmpty();
            softAssertions.assertThat(loginResponse.accessToken()).isNotNull();
        });
    }

    @Test
    @DisplayName("가입된 지원자가 로그인 요청을 하면 액세스 토큰을 발급한다.")
    void loginApplicant() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher).recruitment();
        Applicant applicant = JONGMEE_APPLICANT(recruitment).applicant();
        ApplicantLoginRequest request = new ApplicantLoginRequest(recruitment.getCode(), applicant.getEmail(), applicant.getPassword());

        // when
        LoginResponse loginResponse = authService.loginForApplicant(request);

        // then
        Optional<Applicant> createdApplicant = applicantRepository.findById(loginResponse.userId());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdApplicant).isNotEmpty();
            softAssertions.assertThat(loginResponse.accessToken()).isNotNull();
        });
    }

    @Test
    @DisplayName("액세스 토큰으로 인증된 사용자를 조회한다.")
    void findAuthentication() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher).recruitment();
        Applicant applicant = JONGMEE_APPLICANT(recruitment).applicant();
        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, applicant.getEmail());

        // when
        LoginUser loginUser = authService.findApplicantAuthentication(accessToken);

        // then
        assertThat(loginUser.userId()).isEqualTo(applicant.getId());
    }
}
