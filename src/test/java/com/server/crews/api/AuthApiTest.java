package com.server.crews.api;

import static com.server.crews.api.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.api.StatusCodeChecker.checkStatusCode401;
import static com.server.crews.fixture.UserFixture.TEST_CLUB_NAME;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.server.crews.auth.application.JwtTokenProvider;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.auth.presentation.AuthorizationExtractor;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.ErrorDto;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class AuthApiTest extends ApiTest {
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("[동아리 관리자] 동아리 관리자가 로그인 해 토큰을 발급 받는다.")
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
        TokenResponse tokenResponse = response.as(TokenResponse.class);
        Map<String, String> cookies = response.cookies();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(tokenResponse.accessToken()).isNotEmpty();
            softAssertions.assertThat(cookies.get("refreshToken")).isNotNull();
        });
    }

    @Test
    @DisplayName("[지원자] 지원자가 로그인 해 토큰을 발급 받는다.")
    void loginNotSignedUpApplicant() {
        // given
        ApplicantLoginRequest applicantLoginRequest = new ApplicantLoginRequest(TEST_EMAIL, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(AuthApiDocuments.LOGIN_APPLICANT_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(applicantLoginRequest)
                .when().post("/auth/applicant/login")
                .then().log().all()
                .extract();

        // then
        TokenResponse applicantTokenResponse = response.as(TokenResponse.class);
        Map<String, String> cookies = response.cookies();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicantTokenResponse.accessToken()).isNotEmpty();
            softAssertions.assertThat(cookies.get("refreshToken")).isNotNull();
        });
    }

    @Test
    @DisplayName("[동아리 관리자] 가입한 동아리 관리자가 틀린 비밀번호로 로그인한다.")
    void loginSignedUpAdmin() {
        // given
        signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        AdminLoginRequest adminLoginRequest = new AdminLoginRequest(TEST_CLUB_NAME, "wrong password");

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(AuthApiDocuments.LOGIN_ADMIN_400_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(adminLoginRequest)
                .when().post("/auth/admin/login")
                .then().log().all()
                .extract();

        // then
        ErrorDto errorResponse = response.as(ErrorDto.class);
        assertSoftly(softAssertions -> {
            checkStatusCode401(response, softAssertions);
            softAssertions.assertThat(errorResponse.code()).isEqualTo(CrewsErrorCode.WRONG_PASSWORD.getCode());
        });
    }

    @Test
    @DisplayName("[지원자] 지원자 권한으로 지원자 목록을 조회한다.")
    void authenticateAdminWithApplicantToken() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        TokenResponse applicantTokenResponse = signUpApplicant(TEST_EMAIL, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(AuthApiDocuments.AUTHORIZE_401_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .when().get("/applications?recruitment-id=" + recruitmentDetailsResponse.id())
                .then().log().all()
                .extract();

        // then
        ErrorDto errorResponse = response.as(ErrorDto.class);
        assertSoftly(softAssertions -> {
            checkStatusCode401(response, softAssertions);
            softAssertions.assertThat(errorResponse.code()).isEqualTo(CrewsErrorCode.UNAUTHORIZED_USER.getCode());
        });
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
                .header("Cookie", "refreshToken=" + refreshToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(adminLoginRequest)
                .when().post("/auth/refresh")
                .then().log().all()
                .extract();

        // then
        TokenResponse tokenResponse = response.as(TokenResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response);
            softAssertions.assertThat(tokenResponse.accessToken()).isNotNull();
        });
    }

    @Test
    @DisplayName("로그아웃한다.")
    void logout() {
        // given
        TokenResponse tokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(AuthApiDocuments.LOGOUT_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + tokenResponse.accessToken())
                .when().post("/auth/logout")
                .then().log().all()
                .extract();

        // then
        Cookie cookie = response.detailedCookie("refreshToken");
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(cookie.getMaxAge()).isEqualTo(0);
        });
    }

    @Test
    @DisplayName("존재하지 않는 사용자에 대한 액세스 토큰을 검증한다.")
    void validateNotExistingUserToken() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        String applicantAccessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, "not existing email");

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(AuthApiDocuments.VALIDATE_TOKEN_USER_NOT_FOUND_401_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + applicantAccessToken)
                .queryParam("code", recruitmentDetailsResponse.code())
                .when().get("/applications/mine")
                .then().log().all()
                .extract();

        // then
        ErrorDto errorResponse = response.as(ErrorDto.class);
        assertSoftly(softAssertions -> {
            checkStatusCode401(response, softAssertions);
            softAssertions.assertThat(errorResponse.code()).isEqualTo(CrewsErrorCode.USER_NOT_FOUND.getCode());
        });
    }
}
