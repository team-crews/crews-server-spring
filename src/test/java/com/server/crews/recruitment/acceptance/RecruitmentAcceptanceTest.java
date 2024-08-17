package com.server.crews.recruitment.acceptance;

import static com.server.crews.environ.acceptance.StatusCodeChecker.checkStatusCode200;
import static com.server.crews.fixture.RecruitmentFixture.RECRUITMENT_SAVE_REQUEST;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static com.server.crews.recruitment.acceptance.RecruitmentApiDocuments.RECRUITMENT_SAVE_200_REQUEST_DOCUMENT;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.presentation.AuthorizationExtractor;
import com.server.crews.environ.acceptance.AcceptanceTest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import com.server.crews.recruitment.dto.response.SelectiveQuestionResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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

        // when
        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .filter(RECRUITMENT_SAVE_200_REQUEST_DOCUMENT())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AuthorizationExtractor.BEARER_TYPE + accessToken)
                .body(RECRUITMENT_SAVE_REQUEST)
                .when().post("/recruitments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        RecruitmentDetailsResponse recruitmentDetailsResponse = response.as(RecruitmentDetailsResponse.class);
        assertSoftly(softAssertions -> {
            checkStatusCode200(response, softAssertions);
            softAssertions.assertThat(recruitmentDetailsResponse.id()).isNotNull();
            softAssertions.assertThat(recruitmentDetailsResponse.sections()).flatExtracting(SectionResponse::id)
                    .doesNotContainNull();
            softAssertions.assertThat(recruitmentDetailsResponse.sections())
                    .flatExtracting(SectionResponse::selectiveQuestions)
                    .flatExtracting(SelectiveQuestionResponse::id).doesNotContainNull();
        });
    }
}
