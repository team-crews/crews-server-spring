package com.server.crews.fixture;

import com.server.crews.auth.dto.request.NewRecruitmentRequest;
import com.server.crews.auth.dto.response.TokenResponse;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_SECRET_CODE;

public class TokenFixture {

    public static String RECRUITMENT_ACCESS_TOKEN() {
        TokenResponse tokenResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new NewRecruitmentRequest(DEFAULT_SECRET_CODE))
                .when().post("/auth/recruitment/secret-code")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(TokenResponse.class);
        return tokenResponse.accessToken();
    }

    public static String APPLICANT_ACCESS_TOKEN() {
        TokenResponse tokenResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new NewRecruitmentRequest(DEFAULT_SECRET_CODE))
                .when().post("/auth/applicant/secret-code")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(TokenResponse.class);
        return tokenResponse.accessToken();
    }
}
