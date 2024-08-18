package com.server.crews.api;

import static com.server.crews.api.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_MAJOR;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NAME;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NARRATIVE_ANSWER;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_STUDENT_NUMBER;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.applicant.dto.response.SelectiveAnswerResponse;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.presentation.AuthorizationExtractor;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ApplicationApiTest extends ApiTest {

    @Test
    @DisplayName("지원자가 로그인하여 지원서를 저장한다.")
    void saveApplication() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        AccessTokenResponse applicantTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(), TEST_EMAIL,
                TEST_PASSWORD);
        List<AnswerSaveRequest> firstAnswerSaveRequests = List.of(
                new AnswerSaveRequest(null, QuestionType.NARRATIVE.name(), 2L, DEFAULT_NARRATIVE_ANSWER, null),
                new AnswerSaveRequest(null, QuestionType.SELECTIVE.name(), 1L, null, 2L));
        ApplicationSaveRequest applicationCreateRequest = new ApplicationSaveRequest(null, DEFAULT_STUDENT_NUMBER,
                DEFAULT_MAJOR, DEFAULT_NAME, firstAnswerSaveRequests);
        ApplicationDetailsResponse testApplication = createTestApplication(applicantTokenResponse.accessToken(),
                applicationCreateRequest);
        System.out.println("testApplication: " + testApplication);

        List<AnswerSaveRequest> secondAnswerSaveRequests = List.of(
                new AnswerSaveRequest(1L, QuestionType.NARRATIVE.name(), 2L, "수정된내용", null),
                new AnswerSaveRequest(null, QuestionType.SELECTIVE.name(), 1L, null, 1L));

        ApplicationSaveRequest applicationUpdateRequest = new ApplicationSaveRequest(testApplication.id(),
                DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, DEFAULT_NAME, secondAnswerSaveRequests);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .body(applicationUpdateRequest)
                .when().post("/applications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        ApplicationDetailsResponse applicationDetailsResponse = response.as(ApplicationDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicationDetailsResponse.id()).isNotNull();
            softAssertions.assertThat(applicationDetailsResponse.narrativeAnswers()).hasSize(1);
            softAssertions.assertThat(applicationDetailsResponse.selectiveAnswers()).hasSize(1);
        });
    }

    @Test
    @DisplayName("지원서의 상세 정보를 조회한다.")
    void getApplicationDetails() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        AccessTokenResponse applicantTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(), TEST_EMAIL,
                TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest();
        ApplicationDetailsResponse testApplication = createTestApplication(applicantTokenResponse.accessToken(),
                applicationSaveRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.GET_APPLICATION_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .pathParam("application-id", testApplication.id())
                .when().get("/applications/{application-id}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        ApplicationDetailsResponse applicationDetailsResponse = response.as(ApplicationDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicationDetailsResponse.id()).isNotNull();
            softAssertions.assertThat(applicationDetailsResponse.narrativeAnswers()).isNotEmpty();
            softAssertions.assertThat(applicationDetailsResponse.selectiveAnswers())
                    .flatExtracting(SelectiveAnswerResponse::choices).isNotEmpty();
        });
    }

    @Test
    @DisplayName("지원서들을 평가한다.")
    void evaluate() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        AccessTokenResponse applicantATokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "A" + TEST_EMAIL, TEST_PASSWORD);
        AccessTokenResponse applicantBTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest();
        ApplicationDetailsResponse applicationADetailsResponse = createTestApplication(
                applicantATokenResponse.accessToken(), applicationSaveRequest);
        createTestApplication(applicantBTokenResponse.accessToken(), applicationSaveRequest);

        EvaluationRequest evaluationRequest = new EvaluationRequest(List.of(applicationADetailsResponse.id()));

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.EVALUATE_APPLICATIONS_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(evaluationRequest)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .when().post("/applications/evaluation")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        checkStatusCode200(response);
    }

    @Test
    @DisplayName("한 공고의 모든 지원서 목록을 조회한다.")
    void getAllApplicationsByRecruitment() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        AccessTokenResponse applicantATokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "A" + TEST_EMAIL, TEST_PASSWORD);
        AccessTokenResponse applicantBTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest();
        createTestApplication(applicantATokenResponse.accessToken(), applicationSaveRequest);
        createTestApplication(applicantBTokenResponse.accessToken(), applicationSaveRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.GET_APPLICATIONS_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .when().get("/applications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        List<ApplicationsResponse> applicationsResponses = Arrays.stream(response.as(ApplicationsResponse[].class))
                .toList();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicationsResponses).hasSize(2);
        });
    }
}
