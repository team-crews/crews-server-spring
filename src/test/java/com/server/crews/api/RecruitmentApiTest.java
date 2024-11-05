package com.server.crews.api;

import static com.server.crews.api.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.api.StatusCodeChecker.checkStatusCode204;
import static com.server.crews.api.StatusCodeChecker.checkStatusCode400;
import static com.server.crews.api.StatusCodeChecker.checkStatusCode409;
import static com.server.crews.fixture.QuestionFixture.INTRODUCTION_QUESTION;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_QUESTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DEADLINE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static com.server.crews.fixture.RecruitmentFixture.QUESTION_REQUESTS;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;
import static com.server.crews.fixture.UserFixture.TEST_CLUB_NAME;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.auth.controller.AuthorizationExtractor;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.ErrorResponse;
import com.server.crews.global.exception.GlobalExceptionHandler;
import com.server.crews.recruitment.domain.QuestionType;
import com.server.crews.recruitment.domain.RecruitmentProgress;
import com.server.crews.recruitment.dto.request.ChoiceSaveRequest;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionSaveRequest;
import com.server.crews.recruitment.dto.response.QuestionResponse;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.RecruitmentProgressResponse;
import com.server.crews.recruitment.dto.response.RecruitmentSearchResponse;
import com.server.crews.recruitment.dto.response.RecruitmentStateInProgressResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RecruitmentApiTest extends ApiTest {

    @Test
    @DisplayName("모집 공고를 저장한다.")
    void saveRecruitment() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();
        ChoiceSaveRequest choiceCreateRequest = new ChoiceSaveRequest(null, "선택지 내용");
        QuestionSaveRequest selectiveQuestionCreateRequest = new QuestionSaveRequest(null,
                QuestionType.SELECTIVE.name(),
                STRENGTH_QUESTION, true, 2, null, 1, 2, List.of(choiceCreateRequest));
        SectionSaveRequest sectionsCreateRequest = new SectionSaveRequest(null, FRONTEND_SECTION_NAME,
                DEFAULT_DESCRIPTION,
                List.of(selectiveQuestionCreateRequest));
        RecruitmentSaveRequest recruitmentCreateRequest = new RecruitmentSaveRequest(null, null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, List.of(sectionsCreateRequest), DEFAULT_DEADLINE);

        RecruitmentDetailsResponse savedRecruitmentResponse = createRecruitment(adminTokenResponse.accessToken(),
                recruitmentCreateRequest);

        Long recruitmentId = savedRecruitmentResponse.id();
        String recruitmentCode = savedRecruitmentResponse.code();
        Long sectionId = savedRecruitmentResponse.sections().get(0).id();
        Long questionId = savedRecruitmentResponse.sections().get(0).questions().get(0).id();
        Long choiceId = savedRecruitmentResponse.sections().get(0).questions().get(0).choices().get(0).id();

        ChoiceSaveRequest choiceUpdateRequest = new ChoiceSaveRequest(choiceId, "변경된 선택지 내용");
        QuestionSaveRequest selectiveQuestionUpdateRequest = new QuestionSaveRequest(questionId,
                QuestionType.SELECTIVE.name(), "변경된 질문 내용", true, 2, null, 1, 2, List.of(choiceUpdateRequest));
        SectionSaveRequest sectionUpdateRequest = new SectionSaveRequest(sectionId, "변경된 섹션 이름", DEFAULT_DESCRIPTION,
                List.of(selectiveQuestionUpdateRequest));
        SectionSaveRequest newSectionCreateRequest = new SectionSaveRequest(null, "새로운 섹션 이름", DEFAULT_DESCRIPTION,
                QUESTION_REQUESTS);
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(recruitmentId, recruitmentCode,
                "변경된 모집 공고 제목", DEFAULT_DESCRIPTION, List.of(sectionUpdateRequest, newSectionCreateRequest),
                DEFAULT_DEADLINE);

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
    @DisplayName("모집 공고 필드의 글자수를 검증한다.")
    void saveWithLetterNumberValidation() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(null, null,
                "DEFAULT_TITLE_DEFAULT_TITLE_31_", DEFAULT_DESCRIPTION, List.of(), DEFAULT_DEADLINE);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SAVE_RECRUITMENT_400_DOCUMENT_WRONG_LETTER_LENGTH())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .body(recruitmentSaveRequest)
                .when().post("/recruitments")
                .then().log().all()
                .extract();

        // then
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode400(response, softAssertions);
            softAssertions.assertThat(errorResponse.code()).isEqualTo(GlobalExceptionHandler.CONSTRAINT_VIOLATION_CODE);
        });
    }

    @Test
    @DisplayName("서술형 문항의 최대 글자 수를 검증한다.")
    void validateNarrativeQuestionWordLimit() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);

        QuestionSaveRequest questionSaveRequest = new QuestionSaveRequest(null, QuestionType.NARRATIVE.name(),
                INTRODUCTION_QUESTION, true, 1, 1501, null, null, List.of());
        SectionSaveRequest sectionSaveRequest = new SectionSaveRequest(null, BACKEND_SECTION_NAME, DEFAULT_DESCRIPTION,
                List.of(questionSaveRequest));
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(null, null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, List.of(sectionSaveRequest), DEFAULT_DEADLINE);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SAVE_RECRUITMENT_400_DOCUMENT_WRONG_NARRATIVE_QUESTION_WORD_LIMIT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .body(recruitmentSaveRequest)
                .when().post("/recruitments")
                .then().log().all()
                .extract();

        // then
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode400(response, softAssertions);
            softAssertions.assertThat(errorResponse.code()).isEqualTo(GlobalExceptionHandler.CONSTRAINT_VIOLATION_CODE);
        });
    }

    @Test
    @DisplayName("선택형 문항의 최소, 최대 선택 개수를 검증한다.")
    void validateSelectiveQuestionSelectionCount() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);

        QuestionSaveRequest questionSaveRequest = new QuestionSaveRequest(null, QuestionType.SELECTIVE.name(),
                STRENGTH_QUESTION, true, 1, null, 11, 11, List.of());
        SectionSaveRequest sectionSaveRequest = new SectionSaveRequest(null, BACKEND_SECTION_NAME, DEFAULT_DESCRIPTION,
                List.of(questionSaveRequest));
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(null, null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, List.of(sectionSaveRequest), DEFAULT_DEADLINE);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SAVE_RECRUITMENT_400_DOCUMENT_WRONG_SELECTIVE_QUESTION_SELECTION_COUNT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,
                        AuthorizationExtractor.BEARER_TYPE + adminTokenResponse.accessToken())
                .body(recruitmentSaveRequest)
                .when().post("/recruitments")
                .then().log().all()
                .extract();

        // then
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode400(response, softAssertions);
            softAssertions.assertThat(errorResponse.code()).isEqualTo(GlobalExceptionHandler.CONSTRAINT_VIOLATION_CODE);
        });
    }

    @Test
    @DisplayName("유효하지 않은 마감일로 모집 공고를 저장한다.")
    void saveRecruitmentWithInvalidDeadline() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();

        LocalDateTime invalidDeadline = LocalDateTime.of(2030, 9, 30, 1, 10);
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(null, null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, List.of(), invalidDeadline);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SAVE_RECRUITMENT_400_DOCUMENT_INVALID_DEADLINE())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .body(recruitmentSaveRequest)
                .when().post("/recruitments")
                .then().log().all()
                .extract();

        // then
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode400(response, softAssertions);
            softAssertions.assertThat(errorResponse.code()).isEqualTo(CrewsErrorCode.INVALID_DEADLINE.getCode());
        });
    }

    @Test
    @DisplayName("모집 공고 제목 목록를 prefix로 검색한다.")
    void searchRecruitmentsTitle() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();
        createRecruitment(accessToken);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .when().patch("/recruitments/in-progress");

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SEARCH_RECRUITMENTS_TITLE_200_DOCUMENT())
                .queryParams(Map.of("prefix", "TI", "limit", "3"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/recruitments/search")
                .then().log().all()
                .extract();

        // then
        List<RecruitmentSearchResponse> recruitmentSearchResponses = Arrays.stream(
                response.as(RecruitmentSearchResponse[].class)).toList();
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(recruitmentSearchResponses).hasSize(1)
                    .extracting(RecruitmentSearchResponse::title)
                    .containsExactly(DEFAULT_TITLE);
        });
    }

    @Test
    @DisplayName("모집을 시작한다.")
    void startRecruiting() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
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
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String accessToken = adminTokenResponse.accessToken();
        createRecruitment(accessToken);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .when().patch("/recruitments/in-progress");

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.START_RECRUITMENT_409_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .when().patch("/recruitments/in-progress")
                .then().log().all()
                .extract();

        // then
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode409(response, softAssertions);
            softAssertions.assertThat(errorResponse.code())
                    .isEqualTo(CrewsErrorCode.RECRUITMENT_ALREADY_STARTED.getCode());
        });
    }

    @Test
    @DisplayName("모집 중 지원 상태(지원자 수, 마감일)를 조회한다.")
    void getRecruitmentStateInProgress() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminAccessToken);
        startTestRecruiting(adminAccessToken);
        TokenResponse applicantATokenResponse = signUpApplicant("A" + TEST_EMAIL, TEST_PASSWORD);
        TokenResponse applicantBTokenResponse = signUpApplicant("B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest(recruitmentDetailsResponse.code(), 1l);
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
    @DisplayName("작성중인 모집 공고 및 지원서 양식 상세 정보를 조회한다.")
    void getRecruitmentDetails() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        createRecruitment(adminAccessToken);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.GET_READY_RECRUITMENT_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().get("/recruitments/ready")
                .then().log().all()
                .extract();

        // then
        RecruitmentDetailsResponse recruitmentDetailsResponse = response.as(RecruitmentDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(recruitmentDetailsResponse.id()).isNotNull();
            softAssertions.assertThat(recruitmentDetailsResponse.sections()).isNotEmpty();
            softAssertions.assertThat(recruitmentDetailsResponse.sections())
                    .flatExtracting(SectionResponse::questions)
                    .filteredOn(questionResponse -> questionResponse.type() == QuestionType.NARRATIVE)
                    .isNotEmpty();
            softAssertions.assertThat(recruitmentDetailsResponse.sections())
                    .flatExtracting(SectionResponse::questions)
                    .filteredOn(questionResponse -> questionResponse.type() == QuestionType.SELECTIVE)
                    .flatExtracting(QuestionResponse::choices)
                    .isNotEmpty();
        });
    }

    @Test
    @DisplayName("작성중인 모집공고가 없다면 상세 정보 조회 시 204를 반환한다.")
    void getRecruitmentDetailsWhenNotExisting() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.GET_READY_RECRUITMENT_204_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().get("/recruitments/ready")
                .then().log().all()
                .extract();

        // then
        checkStatusCode204(response);
    }

    @Test
    @DisplayName("모집 공고 및 지원서 양식 상세 정보를 모집 공고 코드로 조회한다.")
    void getRecruitmentDetailsByCode() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse savedRecruitmentDetailsResponse = createRecruitment(adminAccessToken);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().patch("/recruitments/in-progress");

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
    @DisplayName("준비 중인 모집 공고를 코드로 조회한다.")
    void getReadyRecruitmentDetailsByCode() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse savedRecruitmentDetailsResponse = createRecruitment(adminAccessToken);

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.GET_RECRUITMENT_BY_CODE_409_DOCUMENT())
                .queryParam("code", savedRecruitmentDetailsResponse.code())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().get("/recruitments")
                .then().log().all()
                .extract();

        // then
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode409(response, softAssertions);
            softAssertions.assertThat(errorResponse.code()).isEqualTo(CrewsErrorCode.RECRUITMENT_NOT_STARTED.getCode());
        });
    }

    @Test
    @DisplayName("모집 공고 및 지원서 양식 상세 정보를 모집 공고 제목으로 조회한다.")
    void getRecruitmentDetailsByTitle() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse savedRecruitmentDetailsResponse = createRecruitment(adminAccessToken);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().patch("/recruitments/in-progress");

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.GET_RECRUITMENT_BY_TITLE_200_DOCUMENT())
                .queryParam("title", savedRecruitmentDetailsResponse.title())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().get("/recruitments/search-by")
                .then().log().all()
                .extract();

        // then
        checkStatusCode200(response);
    }

    @Test
    @DisplayName("모집 마감기한을 변경한다.")
    void updateDeadline() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminAccessToken);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().patch("/recruitments/in-progress");

        DeadlineUpdateRequest deadlineUpdateRequest = new DeadlineUpdateRequest(
                recruitmentDetailsResponse.deadline().plusDays(1));

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
    @DisplayName("변경할 모집 마감기한은 기존 기한 이후이며 모집 진행 중에만 변경할 수 있다.")
    void updateInvalidDeadline() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminAccessToken);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().patch("/recruitments/in-progress");

        DeadlineUpdateRequest deadlineUpdateRequest = new DeadlineUpdateRequest(
                recruitmentDetailsResponse.deadline().minusDays(1));

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.UPDATE_RECRUITMENT_DEADLINE_400_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .body(deadlineUpdateRequest)
                .when().patch("/recruitments/deadline")
                .then().log().all()
                .extract();

        // then
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode400(response, softAssertions);
            softAssertions.assertThat(errorResponse.code())
                    .isEqualTo(CrewsErrorCode.INVALID_MODIFIED_DEADLINE.getCode());
        });
    }

    @Test
    @DisplayName("모집 공고의 단계를 조회한다.")
    void getRecruitmentProgress() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.GET_RECRUITMENT_PROGRESS_200_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().get("/recruitments/progress")
                .then().log().all()
                .extract();

        // then
        RecruitmentProgressResponse recruitmentProgressResponse = response.as(RecruitmentProgressResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(recruitmentProgressResponse.recruitmentProgress())
                    .isEqualTo(RecruitmentProgress.READY);
        });
    }

    @Test
    @DisplayName("모든 지원자에게 지원 결과 메일을 전송한다.")
    void sendOutcomeEmail() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminAccessToken);
        startTestRecruiting(adminAccessToken);
        TokenResponse applicantATokenResponse = signUpApplicant("A" + TEST_EMAIL, TEST_PASSWORD);
        TokenResponse applicantBTokenResponse = signUpApplicant("B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest(recruitmentDetailsResponse.code(), 1l);
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

    @Test
    @DisplayName("지원 결과 메일은 재전송할 수 없다.")
    void sendDuplicatedOutcomeEmail() {
        // given
        TokenResponse adminTokenResponse = signUpAdmin(TEST_CLUB_NAME, TEST_PASSWORD);
        String adminAccessToken = adminTokenResponse.accessToken();
        RecruitmentDetailsResponse recruitmentDetailsResponse = createRecruitment(adminAccessToken);
        startTestRecruiting(adminAccessToken);
        TokenResponse applicantATokenResponse = signUpApplicant("A" + TEST_EMAIL, TEST_PASSWORD);
        TokenResponse applicantBTokenResponse = signUpApplicant("B" + TEST_EMAIL, TEST_PASSWORD);

        ApplicationSaveRequest applicationSaveRequest = applicationSaveRequest(recruitmentDetailsResponse.code(), 1l);
        createTestApplication(applicantATokenResponse.accessToken(), applicationSaveRequest);
        createTestApplication(applicantBTokenResponse.accessToken(), applicationSaveRequest);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().post("/recruitments/announcement");

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RecruitmentApiDocuments.SEND_OUTCOME_EMAIL_400_REQUEST())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + adminAccessToken)
                .when().post("/recruitments/announcement")
                .then().log().all()
                .extract();

        // then
        ErrorResponse errorReponse = response.as(ErrorResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode409(response, softAssertions);
            softAssertions.assertThat(errorReponse.code()).isEqualTo(CrewsErrorCode.ALREADY_ANNOUNCED.getCode());
        });
    }
}
