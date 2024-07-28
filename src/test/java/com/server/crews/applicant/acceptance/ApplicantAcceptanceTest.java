package com.server.crews.applicant.acceptance;

import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.presentation.AuthorizationExtractor;
import com.server.crews.environ.acceptance.AcceptanceTest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static com.server.crews.environ.acceptance.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.environ.acceptance.StatusCodeChecker.checkStatusCode201;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_MAJOR;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NAME;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NARRATIVE_ANSWER;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_STUDENT_NUMBER;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class ApplicantAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지원자가 로그인하여 지원서를 처음으로 저장한다.")
    void createApplication() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        AccessTokenResponse applicantTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(), TEST_EMAIL, TEST_PASSWORD);

        List<AnswerSaveRequest> answerSaveRequests = List.of(
                new AnswerSaveRequest(QuestionType.NARRATIVE, 2L, DEFAULT_NARRATIVE_ANSWER, List.of()),
                new AnswerSaveRequest(QuestionType.SELECTIVE, 1L, null, List.of(1L, 2L)));
        ApplicationSaveRequest applicationSaveRequest = new ApplicationSaveRequest(DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, DEFAULT_NAME, answerSaveRequests);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .body(applicationSaveRequest)
                .when().post("/applications")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();

        // then
        ApplicationDetailsResponse applicationDetailsResponse = response.as(ApplicationDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode201(response, softAssertions);
            softAssertions.assertThat(applicationDetailsResponse.id()).isNotNull();
            softAssertions.assertThat(applicationDetailsResponse.narrativeAnswers()).hasSize(1);
            softAssertions.assertThat(applicationDetailsResponse.selectiveAnswers()).hasSize(1);
        });
    }

    @Test
    @DisplayName("한 공고의 모든 지원서 목록을 조회한다.")
    void findAllApplicationsByRecruitment() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        AccessTokenResponse applicantATokenResponse = signUpApplicant(recruitmentDetailsResponse.code(), "A" + TEST_EMAIL, TEST_PASSWORD);
        AccessTokenResponse applicantBTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(), "B" + TEST_EMAIL, TEST_PASSWORD);

        List<AnswerSaveRequest> answerSaveRequests = List.of(
                new AnswerSaveRequest(QuestionType.NARRATIVE, 2L, DEFAULT_NARRATIVE_ANSWER, List.of()),
                new AnswerSaveRequest(QuestionType.SELECTIVE, 1L, null, List.of(1L, 2L)));
        ApplicationSaveRequest applicationSaveRequest = new ApplicationSaveRequest(DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, DEFAULT_NAME, answerSaveRequests);
        createTestApplication(applicantATokenResponse.accessToken(), applicationSaveRequest);
        createTestApplication(applicantBTokenResponse.accessToken(), applicationSaveRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .when().get("/applications?recruitment-id=" + recruitmentDetailsResponse.id())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        List<ApplicationsResponse> applicationsResponses = Arrays.stream(response.as(ApplicationsResponse[].class)).toList();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicationsResponses).hasSize(2);
        });
    }
}
