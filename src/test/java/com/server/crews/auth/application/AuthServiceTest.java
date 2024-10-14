package com.server.crews.auth.application;

import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.auth.repository.ApplicantRepository;
import com.server.crews.environ.service.ServiceTest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    @DisplayName("동아리 관리자가 회원가입하고 액세스 토큰을 발급받는다.")
    void loginNotSignedUpAdmin() {
        // given
        String clubName = "멋쟁이사자처럼";
        String password = "new password";
        AdminLoginRequest request = new AdminLoginRequest(clubName, password);

        // when
        TokenResponse tokenResponse = authService.registerForAdmin(request);

        // then
        Optional<Administrator> createdAdmin = administratorRepository.findByClubName(tokenResponse.username());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdAdmin).isNotEmpty();
            softAssertions.assertThat(tokenResponse.accessToken()).isNotNull();
        });
    }

    @Test
    @DisplayName("관리자가 로그인하고 액세스 토큰을 발급한다.")
    void loginAdmin() {
        // given
        Administrator administrator = LIKE_LION_ADMIN().administrator();
        AdminLoginRequest request = new AdminLoginRequest(administrator.getClubName(), TEST_PASSWORD);

        // when
        TokenResponse tokenResponse = authService.loginForAdmin(request);

        // then
        Optional<Administrator> createdAdmin = administratorRepository.findByClubName(tokenResponse.username());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdAdmin).isNotEmpty();
            softAssertions.assertThat(tokenResponse.accessToken()).isNotNull();
        });
    }

    @Test
    @DisplayName("지원자가 회원가입하고 액세스 토큰을 발급한다.")
    void loginNotSignedUpApplicant() {
        // given
        String email = "new@gamil.com";
        String password = "new password";
        ApplicantLoginRequest request = new ApplicantLoginRequest(email, password);

        // when
        TokenResponse tokenResponse = authService.registerForApplicant(request);

        // then
        Optional<Applicant> createdApplicant = applicantRepository.findByEmail(tokenResponse.username());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdApplicant).isNotEmpty();
            softAssertions.assertThat(tokenResponse.accessToken()).isNotNull();
        });
    }

    @Test
    @DisplayName("가입된 지원자가 로그인 요청을 하면 액세스 토큰을 발급한다.")
    void loginApplicant() {
        // given
        Applicant applicant = JONGMEE_APPLICANT().applicant();
        ApplicantLoginRequest request = new ApplicantLoginRequest(applicant.getEmail(), TEST_PASSWORD);

        // when
        TokenResponse tokenResponse = authService.loginForApplicant(request);

        // then
        Optional<Applicant> createdApplicant = applicantRepository.findByEmail(tokenResponse.username());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdApplicant).isNotEmpty();
            softAssertions.assertThat(tokenResponse.accessToken()).isNotNull();
        });
    }

    @Test
    @DisplayName("액세스 토큰으로 인증된 사용자를 조회한다.")
    void findAuthentication() {
        // given
        Applicant applicant = JONGMEE_APPLICANT().applicant();
        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, applicant.getEmail());

        // when
        LoginUser loginUser = authService.findApplicantAuthentication(accessToken);

        // then
        assertThat(loginUser.userId()).isEqualTo(applicant.getId());
    }
}
