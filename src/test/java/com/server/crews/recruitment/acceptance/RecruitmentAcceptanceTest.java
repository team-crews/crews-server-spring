package com.server.crews.recruitment.acceptance;

import static com.server.crews.environ.acceptance.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.environ.acceptance.StatusCodeChecker.checkStatusCode400;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_MAJOR;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NAME;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NARRATIVE_ANSWER;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_STUDENT_NUMBER;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_QUESTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CLOSING_DATE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static com.server.crews.fixture.RecruitmentFixture.QUESTION_REQUESTS;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.presentation.AuthorizationExtractor;
import com.server.crews.environ.acceptance.AcceptanceTest;
import com.server.crews.recruitment.dto.request.ChoiceSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.RecruitmentStateInProgressResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class RecruitmentAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("동아리 운영진이 모집 공고를 저장한다.")
    void saveRecruitment() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();
        ChoiceSaveRequest choiceCreateRequest = new ChoiceSaveRequest(null, "선택지 내용");
        QuestionSaveRequest selectiveQuestionCreateRequest = new QuestionSaveRequest(null,
                QuestionType.SELECTIVE.name(),
                STRENGTH_QUESTION, true, 2, null, 1, 2, List.of(choiceCreateRequest));
        SectionSaveRequest sectionsCreateRequest = new SectionSaveRequest(null, FRONTEND_SECTION_NAME,
                DEFAULT_DESCRIPTION,
                List.of(selectiveQuestionCreateRequest));
        RecruitmentSaveRequest recruitmentCreateRequest = new RecruitmentSaveRequest(null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, List.of(sectionsCreateRequest), DEFAULT_CLOSING_DATE.toString());

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
                DEFAULT_CLOSING_DATE.toString());

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SAVE_RECRUITMENT_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .body(recruitmentSaveRequest)
                .when().post("/recruitments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
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
    void saveRecruitmentWithInvalidClosingDate() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();

        String invalidClosingDate = LocalDateTime.now().minusDays(10).toString();
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, null, invalidClosingDate);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SAVE_RECRUITMENT_400_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .body(recruitmentSaveRequest)
                .when().post("/recruitments")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract();

        // then
        checkStatusCode400(response);
    }

    @Test
    @DisplayName("모집을 시작한다.")
    void startRecruiting() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();
        createRecruitment(accessToken);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.START_RECRUITMENT_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .when().patch("/recruitments/in-progress")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        checkStatusCode200(response);
    }

    @Test
    @DisplayName("작성 중 단계가 아닌 모집 공고(이미 시작된)는 시작할 수 없다.")
    void startInvalidStateRecruitment() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();
        createRecruitment(accessToken);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .when().patch("/recruitments/in-progress")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.START_RECRUITMENT_400_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .when().patch("/recruitments/in-progress")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract();

        // then
        checkStatusCode400(response);
    }

    @Test
    @DisplayName("모집 중 지원 상태(지원자 수, 마감일)를 조회한다.")
    void getRecruitmentStateInProgress() {
        // given
        AccessTokenResponse adminTokenResponse = signUpAdmin(TEST_EMAIL, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminAccessToken);
        AccessTokenResponse applicantATokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "A" + TEST_EMAIL, TEST_PASSWORD);
        AccessTokenResponse applicantBTokenResponse = signUpApplicant(recruitmentDetailsResponse.code(),
                "B" + TEST_EMAIL, TEST_PASSWORD);

        List<AnswerSaveRequest> answerSaveRequests = List.of(
                new AnswerSaveRequest(QuestionType.NARRATIVE, 2L, DEFAULT_NARRATIVE_ANSWER, List.of()),
                new AnswerSaveRequest(QuestionType.SELECTIVE, 1L, null, List.of(1L, 2L)));
        ApplicationSaveRequest applicationSaveRequest = new ApplicationSaveRequest(DEFAULT_STUDENT_NUMBER,
                DEFAULT_MAJOR, DEFAULT_NAME, answerSaveRequests);
        createTestApplication(applicantATokenResponse.accessToken(), applicationSaveRequest);
        createTestApplication(applicantBTokenResponse.accessToken(), applicationSaveRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.GET_RECRUITMENT_STATUS_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().get("/recruitments/in-progress")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        RecruitmentStateInProgressResponse recruitmentStateInProgressResponse = response.as(
                RecruitmentStateInProgressResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(recruitmentStateInProgressResponse.closingDate()).isNotNull();
            softAssertions.assertThat(recruitmentStateInProgressResponse.applicationCount()).isEqualTo(2);
        });

    }
}
