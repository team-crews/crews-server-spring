package com.server.crews.fixture;

import com.server.crews.auth.dto.request.NewApplicantRequest;
import com.server.crews.auth.dto.request.NewRecruitmentRequest;
import com.server.crews.auth.dto.response.LoginResponse;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;

public class TokenFixture {

    public static LoginResponse RECRUITMENT_ID_AND_ACCESS_TOKEN() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new NewRecruitmentRequest(DEFAULT_CODE))
                .when().post("/auth/admin/secret-code")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(LoginResponse.class);
    }

    public static String APPLICANT_ID_AND_ACCESS_TOKEN(final Long recruitmentId) {
        LoginResponse loginResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new NewApplicantRequest(DEFAULT_CODE, recruitmentId))
                .when().post("/auth/applicant/secret-code")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(LoginResponse.class);
        return loginResponse.accessToken();
    }
}
