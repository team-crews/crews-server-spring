package com.server.crews.api;

import static com.server.crews.api.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.api.StatusCodeChecker.checkStatusCode204;
import static com.server.crews.api.StatusCodeChecker.checkStatusCode404;
import static com.server.crews.api.StatusCodeChecker.checkStatusCode409;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_MAJOR;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NAME;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NARRATIVE_ANSWER;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_STUDENT_NUMBER;
import static com.server.crews.fixture.UserFixture.TEST_CLUB_NAME;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.AnswerResponse;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.auth.dto.response.TokenResponse;
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
import org.springframework.http.MediaType;

public class ApplicationApiTest extends ApiTest {

    @Test
    @DisplayName("지원자가 로그인하여 지원서를 저장한다.")
    void saveApplication() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        startTestRecruiting(adminTokenResponse.accessToken());
        TokenResponse applicantTokenResponse = signUpApplicant(TEST_EMAIL, TEST_PASSWORD);
        List<AnswerSaveRequest> firstAnswerSaveRequests = List.of(
                new AnswerSaveRequest(null, QuestionType.NARRATIVE.name(), 2L, DEFAULT_NARRATIVE_ANSWER, null),
                new AnswerSaveRequest(null, QuestionType.SELECTIVE.name(), 1L, null, 2L));
        ApplicationSaveRequest applicationCreateRequest = new ApplicationSaveRequest(null, DEFAULT_STUDENT_NUMBER,
                DEFAULT_MAJOR, DEFAULT_NAME, firstAnswerSaveRequests, recruitmentDetailsResponse.code());
        ApplicationDetailsResponse testApplication = createTestApplication(applicantTokenResponse.accessToken(),
                applicationCreateRequest);

        List<AnswerSaveRequest> secondAnswerSaveRequests = List.of(
                new AnswerSaveRequest(1L, QuestionType.NARRATIVE.name(), 2L, "수정된내용", null),
                new AnswerSaveRequest(null, QuestionType.SELECTIVE.name(), 1L, null, 1L));

        ApplicationSaveRequest applicationUpdateRequest = new ApplicationSaveRequest(testApplication.id(),
                DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, DEFAULT_NAME, secondAnswerSaveRequests,
                recruitmentDetailsResponse.code());

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.SAVE_APPLICATION_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .body(applicationUpdateRequest)
                .when().post("/applications")
                .then().log().all()
                .extract();

        // then
        ApplicationDetailsResponse applicationDetailsResponse = response.as(ApplicationDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicationDetailsResponse.id()).isNotNull();
            softAssertions.assertThat(applicationDetailsResponse.answers())
                    .filteredOn(answerResponse -> answerResponse.type() == QuestionType.NARRATIVE)
                    .flatExtracting(AnswerResponse::answerId)
                    .containsExactly(1L);
            softAssertions.assertThat(applicationDetailsResponse.answers())
                    .filteredOn(answerResponse -> answerResponse.type() == QuestionType.SELECTIVE)
                    .flatExtracting(AnswerResponse::choiceId)
                    .hasSize(1);
        });
    }

    @Test
    @DisplayName("지원자가 모집이 시작되지 않은 지원서를 저장한다.")
    void saveApplicationAboutNotStartedRecruitment() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        TokenResponse applicantTokenResponse = signUpApplicant(TEST_EMAIL, TEST_PASSWORD);
        ApplicationSaveRequest applicationUpdateRequest = new ApplicationSaveRequest(null, DEFAULT_STUDENT_NUMBER,
                DEFAULT_MAJOR, DEFAULT_NAME, List.of(), recruitmentDetailsResponse.code());

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.SAVE_APPLICATION_NOT_STARTED_409_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .body(applicationUpdateRequest)
                .when().post("/applications")
                .then().log().all()
                .extract();

        // then
        checkStatusCode409(response);
    }

    @Test
    @DisplayName("지원자가 모집이 시작되지 않은 지원서를 저장한다.")
    void saveApplicationAboutClosedRecruitment() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        closeTestRecruiting(recruitmentDetailsResponse.id());

        TokenResponse applicantTokenResponse = signUpApplicant(TEST_EMAIL, TEST_PASSWORD);
        ApplicationSaveRequest applicationUpdateRequest = new ApplicationSaveRequest(null, DEFAULT_STUDENT_NUMBER,
                DEFAULT_MAJOR, DEFAULT_NAME, List.of(), recruitmentDetailsResponse.code());

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.SAVE_APPLICATION_CLOSED_RECRUITMENT_409_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .body(applicationUpdateRequest)
                .when().post("/applications")
                .then().log().all()
                .extract();

        // then
        checkStatusCode409(response);
    }

    @Test
    @DisplayName("존재하지 않는 질문에 대해 답변을 저장한다.")
    void saveApplicationWithNotExistingQuestion() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        startTestRecruiting(adminTokenResponse.accessToken());
        TokenResponse applicantTokenResponse = signUpApplicant(TEST_EMAIL, TEST_PASSWORD);
        List<AnswerSaveRequest> answerSaveRequests = List.of(
                new AnswerSaveRequest(null, QuestionType.NARRATIVE.name(), 10L, DEFAULT_NARRATIVE_ANSWER, null));
        ApplicationSaveRequest applicationSaveRequest = new ApplicationSaveRequest(null, DEFAULT_STUDENT_NUMBER,
                DEFAULT_MAJOR, DEFAULT_NAME, answerSaveRequests, recruitmentDetailsResponse.code());

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.SAVE_APPLICATION_404_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .body(applicationSaveRequest)
                .when().post("/applications")
                .then().log().all()
                .extract();

        // then
        checkStatusCode404(response);
    }

    @Test
    @DisplayName("동아리 관리자가 지원서 상세 정보를 조회한다.")
    void getApplicationDetails() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        startTestRecruiting(adminTokenResponse.accessToken());
        TokenResponse applicantTokenResponse = signUpApplicant(TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest(recruitmentDetailsResponse.code());
        ApplicationDetailsResponse testApplication = createTestApplication(applicantTokenResponse.accessToken(),
                applicationSaveRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.GET_APPLICATION_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .pathParam("application-id", testApplication.id())
                .when().get("/applications/{application-id}")
                .then().log().all()
                .extract();

        // then
        ApplicationDetailsResponse applicationDetailsResponse = response.as(ApplicationDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicationDetailsResponse.id()).isNotNull();
            softAssertions.assertThat(applicationDetailsResponse.answers())
                    .filteredOn(answerResponse -> answerResponse.type() == QuestionType.NARRATIVE)
                    .isNotEmpty();
            softAssertions.assertThat(applicationDetailsResponse.answers())
                    .filteredOn(answerResponse -> answerResponse.type() == QuestionType.SELECTIVE)
                    .flatExtracting(AnswerResponse::choiceId)
                    .isNotEmpty();
        });
    }

    @Test
    @DisplayName("지원자가 본인의 지원서 상세 정보를 조회한다.")
    void getMyApplicationDetails() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        startTestRecruiting(adminTokenResponse.accessToken());
        TokenResponse applicantTokenResponse = signUpApplicant(TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest(recruitmentDetailsResponse.code());
        createTestApplication(applicantTokenResponse.accessToken(), applicationSaveRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.GET_MY_APPLICATION_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .queryParam("code", recruitmentDetailsResponse.code())
                .when().get("/applications/mine")
                .then().log().all()
                .extract();

        // then
        ApplicationDetailsResponse applicationDetailsResponse = response.as(ApplicationDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(applicationDetailsResponse.id()).isNotNull();
            softAssertions.assertThat(applicationDetailsResponse.answers())
                    .filteredOn(answerResponse -> answerResponse.type() == QuestionType.NARRATIVE)
                    .isNotEmpty();
            softAssertions.assertThat(applicationDetailsResponse.answers())
                    .filteredOn(answerResponse -> answerResponse.type() == QuestionType.SELECTIVE)
                    .flatExtracting(AnswerResponse::choiceId)
                    .isNotEmpty();
        });
    }

    @Test
    @DisplayName("지원자가 아직 작성하지 않은 본인의 지원서 상세 정보를 조회한다.")
    void getMyApplicationDetailsNotExisting() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        TokenResponse applicantTokenResponse = signUpApplicant(TEST_EMAIL, TEST_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.GET_MY_APPLICATION_204_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + applicantTokenResponse.accessToken())
                .queryParam("code", recruitmentDetailsResponse.code())
                .when().get("/applications/mine")
                .then().log().all()
                .extract();

        // then
        checkStatusCode204(response);
    }

    @Test
    @DisplayName("지원서들을 평가한다.")
    void evaluate() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        startTestRecruiting(adminTokenResponse.accessToken());
        TokenResponse applicantATokenResponse = signUpApplicant("A" + TEST_EMAIL, TEST_PASSWORD);
        TokenResponse applicantBTokenResponse = signUpApplicant("B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest(recruitmentDetailsResponse.code());
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
                .extract();

        // then
        checkStatusCode200(response);
    }

    @Test
    @DisplayName("평가 완료된 모집 공고의 지원서들을 평가한다.")
    void evaluateWhenRecruitmentIsAnnounced() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        startTestRecruiting(adminTokenResponse.accessToken());
        TokenResponse applicantATokenResponse = signUpApplicant("A" + TEST_EMAIL, TEST_PASSWORD);
        TokenResponse applicantBTokenResponse = signUpApplicant("B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest(recruitmentDetailsResponse.code());
        ApplicationDetailsResponse applicationADetailsResponse = createTestApplication(
                applicantATokenResponse.accessToken(), applicationSaveRequest);
        createTestApplication(applicantBTokenResponse.accessToken(), applicationSaveRequest);
        EvaluationRequest evaluationRequest = new EvaluationRequest(List.of(applicationADetailsResponse.id()));

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .when().post("/recruitments/announcement");

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(ApplicationApiDocuments.EVALUATE_APPLICATIONS_409_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(evaluationRequest)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .when().post("/applications/evaluation")
                .then().log().all()
                .extract();

        // then
        checkStatusCode409(response);
    }

    @Test
    @DisplayName("한 공고의 모든 지원서 목록을 조회한다.")
    void getAllApplicationsByRecruitment() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminTokenResponse.accessToken());
        startTestRecruiting(adminTokenResponse.accessToken());
        TokenResponse applicantATokenResponse = signUpApplicant("A" + TEST_EMAIL, TEST_PASSWORD);
        TokenResponse applicantBTokenResponse = signUpApplicant("B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest(recruitmentDetailsResponse.code());
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
