package com.server.crews.api;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class AuthApiDocuments {
    private static final String AUTH_API = "auth/";

    public static RestDocumentationFilter LOGIN_ADMIN_200_DOCUMENT() {
        return document(AUTH_API + "동아리 운영진 로그인",
                requestFields(
                        fieldWithPath(".clubName")
                                .description("동아리 이름"),
                        fieldWithPath(".password")
                                .description("비밀번호")),
                responseFields(
                        fieldWithPath(".adminId")
                                .description("운영진 id"),
                        fieldWithPath(".accessToken")
                                .description("access token"),
                        fieldWithPath(".recruitmentProgress")
                                .description(
                                        "모집 공고 상태 (READY: 모집 공고 작성 중, IN_PROGRESS: 모집 중, COMPLETION: 평가 중, ANNOUNCED: 이메일 전송 완료)"),
                        fieldWithPath(".recruitmentId")
                                .description("모집 공고 id (없다면 null)")
                ),
                responseCookies(
                        cookieWithName("refreshToken")
                                .description("리프레시 토큰")
                ));
    }

    public static RestDocumentationFilter LOGIN_APPLICANT_200_DOCUMENT() {
        return document(AUTH_API + "지원자 로그인",
                requestFields(
                        fieldWithPath(".recruitmentCode")
                                .description("모집 공고 코드"),
                        fieldWithPath(".email")
                                .description("이메일"),
                        fieldWithPath(".password")
                                .description("비밀번호")),
                responseFields(
                        fieldWithPath(".applicantId")
                                .description("지원자 id"),
                        fieldWithPath(".accessToken")
                                .description("access token"),
                        fieldWithPath(".recruitmentProgress")
                                .description("모집 공고 상태 (IN_PROGRESS: 모집 중, COMPLETION: 평가 중)"),
                        fieldWithPath(".applicationId")
                                .description("지원서 id (없다면 null)")
                ),
                responseCookies(
                        cookieWithName("refreshToken")
                                .description("리프레시 토큰")
                ));
    }

    public static RestDocumentationFilter REFRESH_TOKEN_200_DOCUMENT() {
        return document(AUTH_API + "토큰 재발급",
                requestCookies(cookieWithName("refreshToken")
                        .description("리프레시 토큰")));
    }

    public static RestDocumentationFilter AUTHORIZE_401_DOCUMENT() {
        return document(AUTH_API + "인가 실패 예시");
    }
}
