package com.server.crews.api;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class AuthApiDocuments {
    private static final String AUTH_API = "auth/";

    public static RestDocumentationFilter LOGIN_ADMIN_200_DOCUMENT() {
        return document(AUTH_API + "동아리 운영진 로그인",
                "동아리 운영진(admin)이 로그인한다.",
                requestFields(
                        fieldWithPath(".clubName").description("동아리 이름"),
                        fieldWithPath(".password").description("비밀번호")),
                responseFields(
                        fieldWithPath(".username").description("동아리 이름"),
                        fieldWithPath(".accessToken").description("access token")),
                responseHeaders(
                        headerWithName("Set-Cookie").description("리프레시 토큰")));
    }

    public static RestDocumentationFilter LOGIN_APPLICANT_200_DOCUMENT() {
        return document(AUTH_API + "지원자 로그인",
                "지원자(applicant)가 로그인한다.",
                requestFields(
                        fieldWithPath(".email").description("이메일"),
                        fieldWithPath(".password").description("비밀번호")),
                responseFields(
                        fieldWithPath(".username").description("지원자 email"),
                        fieldWithPath(".accessToken").description("access token")),
                responseHeaders(
                        headerWithName("Set-Cookie").description("리프레시 토큰")));
    }

    public static RestDocumentationFilter LOGIN_ADMIN_400_DOCUMENT() {
        return document(AUTH_API + "동아리 관리자 로그인 비밀번호 불일치");
    }

    public static RestDocumentationFilter REFRESH_TOKEN_200_DOCUMENT() {
        return document(AUTH_API + "토큰 재발급",
                "access token을 재발급 받는다.",
                requestHeaders(
                        headerWithName("Cookie").description("리프레시 토큰")),
                responseFields(
                        fieldWithPath(".accessToken").description("access token")));
    }

    public static RestDocumentationFilter LOGOUT_200_DOCUMENT() {
        return document(AUTH_API + "로그아웃",
                "로그아웃한다.",
                responseHeaders(
                        headerWithName("Set-Cookie").description("만료된 쿠키")));
    }

    public static RestDocumentationFilter AUTHORIZE_401_DOCUMENT() {
        return document(AUTH_API + "인가 실패 예시");
    }
}
