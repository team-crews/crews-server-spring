package com.server.crews.api;

import static com.server.crews.api.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.api.StatusCodeChecker.checkStatusCode400;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_QUESTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DEADLINE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static com.server.crews.fixture.RecruitmentFixture.QUESTION_REQUESTS;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;
import static com.server.crews.fixture.UserFixture.TEST_CLUB_NAME;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.auth.dto.response.AdminLoginResponse;
import com.server.crews.auth.dto.response.ApplicantLoginResponse;
import com.server.crews.auth.presentation.AuthorizationExtractor;
import com.server.crews.recruitment.dto.request.ChoiceSaveRequest;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.RecruitmentStateInProgressResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import com.server.crews.recruitment.dto.response.SelectiveQuestionResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RecruitmentApiTest extends ApiTest {

    @Test
    @DisplayName("모집 공고를 저장한다.")
    void saveRecruitment() {
        // given
        AdminLoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();
        ChoiceSaveRequest choiceCreateRequest = new ChoiceSaveRequest(null, "선택지 내용");
        QuestionSaveRequest selectiveQuestionCreateRequest = new QuestionSaveRequest(null,
                QuestionType.SELECTIVE.name(),
                STRENGTH_QUESTION, true, 2, null, 1, 2, List.of(choiceCreateRequest));
        SectionSaveRequest sectionsCreateRequest = new SectionSaveRequest(null, FRONTEND_SECTION_NAME,
                DEFAULT_DESCRIPTION,
                List.of(selectiveQuestionCreateRequest));
        RecruitmentSaveRequest recruitmentCreateRequest = new RecruitmentSaveRequest(null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, List.of(sectionsCreateRequest), DEFAULT_DEADLINE.toString());

        RecruitmentDetailsResponse savedRecruitmentResponse = createRecruitment(adminTokenResponse.accessToken(),
                recruitmentCreateRequest);

        Long recruitmentId = savedRecruitmentResponse.id();
        Long sectionId = savedRecruitmentResponse.sections().get(0).id();
        Long questionId = savedRecruitmentResponse.sections().get(0).selectiveQuestions().get(0).id();
        Long choiceId = savedRecruitmentResponse.sections().get(0).selectiveQuestions().get(0).choices().get(0).id();

        ChoiceSaveRequest choiceUpdateRequest = new ChoiceSaveRequest(choiceId, "변경된 선택지 내용");
        QuestionSaveRequest selectiveQuestionUpdateRequest = new QuestionSaveRequest(questionId,
                QuestionType.SELECTIVE.name(), "변경된 질문 내용", true, 2, null, 1, 2, List.of(choiceUpdateRequest));
        SectionSaveRequest sectionUpdateRequest = new SectionSaveRequest(sectionId, "변경된 섹션 이름", DEFAULT_DESCRIPTION,
                List.of(selectiveQuestionUpdateRequest));
        SectionSaveRequest newSectionCreateRequest = new SectionSaveRequest(null, "새로운 섹션 이름", DEFAULT_DESCRIPTION,
                QUESTION_REQUESTS);
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(recruitmentId, "변경된 모집 공고 제목",
                DEFAULT_DESCRIPTION, List.of(sectionUpdateRequest, newSectionCreateRequest),
                DEFAULT_DEADLINE.toString());

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SAVE_RECRUITMENT_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .body(recruitmentSaveRequest)
                .when().post("/recruitments")
                .then().log().all()
                .extract();

        // then
        RecruitmentDetailsResponse recruitmentDetailsResponse = response.as(RecruitmentDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(recruitmentDetailsResponse.id()).isEqualTo(recruitmentId);
        });
    }

    @Test
    @DisplayName("유효하지 않은 마감일로 모집 공고를 저장한다.")
    void saveRecruitmentWithInvalidDeadline() {
        // given
        AdminLoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();

        String date = LocalDate.now().plusDays(10).toString();
        String time = LocalTime.of(1, 10).toString();
        String invalidDeadline = date + "T" + time;
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, null, invalidDeadline);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SAVE_RECRUITMENT_400_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .body(recruitmentSaveRequest)
                .when().post("/recruitments")
                .then().log().all()
                .extract();

        // then
        checkStatusCode400(response);
    }

    @Test
    @DisplayName("모집을 시작한다.")
    void startRecruiting() {
        // given
        AdminLoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();
        createRecruitment(accessToken);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.START_RECRUITMENT_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .when().patch("/recruitments/in-progress")
                .then().log().all()
                .extract();

        // then
        checkStatusCode200(response);
    }

    @Test
    @DisplayName("작성 중 단계가 아닌 모집 공고(이미 시작된)는 시작할 수 없다.")
    void startInvalidStateRecruitment() {
        // given
        AdminLoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();
        createRecruitment(accessToken);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .when().patch("/recruitments/in-progress")
                .then()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.START_RECRUITMENT_400_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .when().patch("/recruitments/in-progress")
                .then().log().all()
                .extract();

        // then
        checkStatusCode400(response);
    }

    @Test
    @DisplayName("모집 중 지원 상태(지원자 수, 마감일)를 조회한다.")
    void getRecruitmentStateInProgress() {
        // given
        AdminLoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminAccessToken);
        ApplicantLoginResponse applicantATokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "A" + TEST_EMAIL, TEST_PASSWORD);
        ApplicantLoginResponse applicantBTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest();
        createTestApplication(applicantATokenResponse.accessToken(), applicationSaveRequest);
        createTestApplication(applicantBTokenResponse.accessToken(), applicationSaveRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.GET_RECRUITMENT_STATUS_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().get("/recruitments/in-progress")
                .then().log().all()
                .extract();

        // then
        RecruitmentStateInProgressResponse recruitmentStateInProgressResponse = response.as(
                RecruitmentStateInProgressResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(recruitmentStateInProgressResponse.deadline()).isNotNull();
            softAssertions.assertThat(recruitmentStateInProgressResponse.applicationCount()).isEqualTo(2);
        });
    }

    @Test
    @DisplayName("모집 공고 및 지원서 양식 상세 정보를 조회한다.")
    void getRecruitmentDetails() {
        // given
        AdminLoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse savedRecruitmentDetailsResponse = createRecruitment(adminAccessToken);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.GET_RECRUITMENT_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .pathParam("recruitment-id", savedRecruitmentDetailsResponse.id())
                .when().get("/recruitments/{recruitment-id}")
                .then().log().all()
                .extract();

        // then
        RecruitmentDetailsResponse recruitmentDetailsResponse = response.as(RecruitmentDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(recruitmentDetailsResponse.id()).isNotNull();
            softAssertions.assertThat(recruitmentDetailsResponse.sections()).isNotEmpty();
            softAssertions.assertThat(recruitmentDetailsResponse.sections())
                    .flatExtracting(SectionResponse::narrativeQuestions).isNotEmpty();
            softAssertions.assertThat(recruitmentDetailsResponse.sections())
                    .flatExtracting(SectionResponse::selectiveQuestions)
                    .flatExtracting(SelectiveQuestionResponse::choices).isNotEmpty();
        });
    }

    @Test
    @DisplayName("모집 공고 및 지원서 양식 상세 정보를 모집 공고 코드로 조회한다.")
    void getRecruitmentDetailsByCode() {
        // given
        AdminLoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse savedRecruitmentDetailsResponse = createRecruitment(adminAccessToken);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.GET_RECRUITMENT_BY_CODE_200_DOCUMENT())
                .queryParam("code", savedRecruitmentDetailsResponse.code())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().get("/recruitments")
                .then().log().all()
                .extract();

        // then
        checkStatusCode200(response);
    }

    @Test
    @DisplayName("모집 마감기한을 변경한다.")
    void updateDeadline() {
        // given
        AdminLoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        createRecruitment(adminAccessToken);

        DeadlineUpdateRequest deadlineUpdateRequest = new DeadlineUpdateRequest(
                LocalDateTime.of(2030, 8, 5, 18, 0).toString());

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.UPDATE_RECRUITMENT_DEADLINE_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .body(deadlineUpdateRequest)
                .when().patch("/recruitments/deadline")
                .then().log().all()
                .extract();

        // then
        checkStatusCode200(response);
    }

    @Test
    @DisplayName("모든 지원자에게 지원 결과 메일을 전송한다.")
    void sendOutcomeEmail() {
        // given
        AdminLoginResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminAccessToken);
        ApplicantLoginResponse applicantATokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "A" + TEST_EMAIL, TEST_PASSWORD);
        ApplicantLoginResponse applicantBTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest();
        createTestApplication(applicantATokenResponse.accessToken(), applicationSaveRequest);
        createTestApplication(applicantBTokenResponse.accessToken(), applicationSaveRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SEND_OUTCOME_EMAIL_200_REQUEST())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().post("/recruitments/announcement")
                .then().log().all()
                .extract();

        // then
        checkStatusCode200(response);
    }
}
