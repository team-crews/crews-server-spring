package com.server.crews.environ.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusCodeChecker {

    public static void checkStatusCode200(ExtractableResponse<Response> response, SoftAssertions softAssertions) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void checkStatusCode201(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void checkStatusCode400(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void checkStatusCode401(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    public static void checkStatusCode404(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static void checkStatusCode500(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
