package com.server.crews.environ.acceptance;

import static com.server.crews.fixture.RecruitmentFixture.RECRUITMENT_SAVE_REQUEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.presentation.AuthorizationExtractor;
import com.server.crews.environ.DatabaseCleaner;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class AcceptanceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    protected RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
        databaseCleaner.clear();
    }

    protected AccessTokenResponse signUpAdmin(String email, String password) {
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new AdminLoginRequest(email, password))
                .when().post("/auth/admin/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract();
        return response.as(AccessTokenResponse.class);
    }

    protected RecruitmentDetailsResponse createRecruitment(String accessToken) {
        return createRecruitment(accessToken, RECRUITMENT_SAVE_REQUEST);
    }

    protected RecruitmentDetailsResponse createRecruitment(String accessToken, RecruitmentSaveRequest request) {
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .body(request)
                .when().post("/recruitments")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract();
        return response.as(RecruitmentDetailsResponse.class);
    }

    protected AccessTokenResponse signUpApplicant(String recruitmentCode, String email, String password) {
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ApplicantLoginRequest(recruitmentCode, email, password))
                .when().post("/auth/applicant/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract();
        return response.as(AccessTokenResponse.class);
    }

    protected ApplicationDetailsResponse createTestApplication(String accessToken,
                                                               ApplicationSaveRequest applicationSaveRequest) {
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .body(applicationSaveRequest)
                .when().post("/applications")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
        return response.as(ApplicationDetailsResponse.class);
    }
}
