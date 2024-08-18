package com.server.crews.api;

import static com.server.crews.api.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.api.StatusCodeChecker.checkStatusCode401;
import static com.server.crews.fixture.UserFixture.TEST_CLUB_NAME;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.LoginResponse;
import com.server.crews.auth.presentation.AuthorizationExtractor;
import com.server.crews.recruitment.domain.Progress;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class AuthApiTest extends ApiTest {

    @Test
    @DisplayName("[동아리 관리자] 가입하지 않은 동아리 관리자가 로그인 해 토큰을 발급 받는다.")
    void loginNotSignedUpAdmin() {
        // given
        AdminLoginRequest adminLoginRequest = new AdminLoginRequest(TEST_CLUB_NAME, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(AuthApiDocuments.LOGIN_ADMIN_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(adminLoginRequest)
                .when().post("/auth/admin/login")
                .then().log().all()
                .extract();

        // then
        LoginResponse loginResponse = response.as(LoginResponse.class);
        Map<String, String> cookies = response.cookies();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(loginResponse.accessToken()).isNotEmpty();
            softAssertions.assertThat(cookies.get("refreshToken")).isNotNull();
            softAssertions.assertThat(loginResponse.progress()).isEqualTo(Progress.READY);
        });
    }

    @Test
    @DisplayName("[동아리 관리자] 가입한 동아리 관리자가 로그인 해 토큰을 발급 받는다.")
    void loginSignedUpAdmin() {
        // given
        signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        AdminLoginRequest adminLoginRequest = new AdminLoginRequest(TEST_CLUB_NAME, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(adminLoginRequest)
                .when().post("/auth/admin/login")
                .then().log().all()
                .extract();

        // then
        LoginResponse loginResponse = response.as(LoginResponse.class);
        Map<String, String> cookies = response.cookies();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(loginResponse.accessToken()).isNotEmpty();
            softAssertions.assertThat(cookies.get("refreshToken")).isNotNull();
            softAssertions.assertThat(loginResponse.progress()).isEqualTo(Progress.READY);
        });
    }

    @Test
    @DisplayName("[지원자] 가입하지 않은 지원자가 로그인 해 토큰을 발급 받는다.")
    void loginNotSignedUpApplicant() {
        // given
        LoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());

        ApplicantLoginRequest applicantLoginRequest = new ApplicantLoginRequest(recruitmentDetailsResponse.code(),
                TEST_EMAIL, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(AuthApiDocuments.LOGIN_APPLICANT_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(applicantLoginRequest)
                .when().post("/auth/applicant/login")
                .then().log().all()
                .extract();

        // then
        LoginResponse loginResponse = response.as(LoginResponse.class);
        Map<String, String> cookies = response.cookies();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(loginResponse.accessToken()).isNotEmpty();
            softAssertions.assertThat(cookies.get("refreshToken")).isNotNull();
            softAssertions.assertThat(loginResponse.progress()).isEqualTo(Progress.IN_PROGRESS);
        });
    }

    @Test
    @DisplayName("[동아리 관리자] 동아리 관리자 권한으로 지원자 목록을 조회한다.")
    void authenticateAdmin() {
        // given
        LoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .when().get("/applications?recruitment-id=" + recruitmentDetailsResponse.id())
                .then().log().all()
                .extract();

        // then
        List<ApplicationsResponse> applicationsResponses = Arrays.stream(response.as(ApplicationsResponse[].class))
                .toList();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicationsResponses).hasSize(0);
        });
    }

    @Test
    @DisplayName("[지원자] 지원자 권한으로 지원자 목록을 조회한다.")
    void authenticateAdminWithApplicantToken() {
        // given
        LoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        LoginResponse applicantTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(), TEST_EMAIL,
                TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .when().get("/applications?recruitment-id=" + recruitmentDetailsResponse.id())
                .then().log().all()
                .extract();

        // then
        checkStatusCode401(response);
    }

    @Test
    @DisplayName("액세스 토큰을 재발급 받는다.")
    void refreshAccessToken() {
        // given
        AdminLoginRequest adminLoginRequest = new AdminLoginRequest(TEST_CLUB_NAME, TEST_PASSWORD);

        String refreshToken = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(adminLoginRequest)
                .when().post("/auth/admin/login")
                .then()
                .extract()
                .cookie("refreshToken");

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(AuthApiDocuments.REFRESH_TOKEN_200_DOCUMENT())
                .cookie("refreshToken", refreshToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(adminLoginRequest)
                .when().post("/auth/refresh")
                .then().log().all()
                .extract();

        // then
        LoginResponse loginResponse = response.as(LoginResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response);
            softAssertions.assertThat(loginResponse.accessToken()).isNotNull();
        });
    }
}
