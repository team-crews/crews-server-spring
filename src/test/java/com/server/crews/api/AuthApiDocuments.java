package com.server.crews.api;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class AuthApiDocuments {
    private static final String AUTH_API = "auth/";

    public static RestDocumentationFilter LOGIN_ADMIN_200_DOCUMENT() {
        return document(AUTH_API + "동아리 운영진 로그인",
                new ResourceSnippetParametersBuilder().description("동아리 운영진(admin)이 로그인한다.")
                        .requestFields(
                                fieldWithPath(".clubName")
                                        .description("동아리 이름"),
                                fieldWithPath(".password")
                                        .description("비밀번호"))
                        .responseFields(
                                fieldWithPath(".adminId")
                                        .description("운영진 id"),
                                fieldWithPath(".accessToken")
                                        .description("access token"),
                                fieldWithPath(".recruitmentProgress")
                                        .description(
                                                "모집 공고 상태 (READY: 모집 공고 작성 중, IN_PROGRESS: 모집 중, COMPLETION: 평가 중, ANNOUNCED: 이메일 전송 완료)"),
                                fieldWithPath(".recruitmentId")
                                        .description("모집 공고 id (없다면 null)"))
                        .responseHeaders(headerWithName("Cookie")
                                .description("리프레시 토큰")));
    }

    public static RestDocumentationFilter LOGIN_APPLICANT_200_DOCUMENT() {
        return document(AUTH_API + "지원자 로그인",
                new ResourceSnippetParametersBuilder().description("지원자(applicant)가 로그인한다.")
                        .requestFields(
                                fieldWithPath(".recruitmentCode")
                                        .description("모집 공고 코드"),
                                fieldWithPath(".email")
                                        .description("이메일"),
                                fieldWithPath(".password")
                                        .description("비밀번호"))
                        .responseFields(
                                fieldWithPath(".applicantId")
                                        .description("지원자 id"),
                                fieldWithPath(".accessToken")
                                        .description("access token"),
                                fieldWithPath(".recruitmentProgress")
                                        .description("모집 공고 상태 (IN_PROGRESS: 모집 중, COMPLETION: 평가 중)"),
                                fieldWithPath(".applicationId")
                                        .description("지원서 id (없다면 null)")));
    }

    public static RestDocumentationFilter REFRESH_TOKEN_200_DOCUMENT() {
        return document(AUTH_API + "토큰 재발급",
                new ResourceSnippetParametersBuilder().description("access token을 재발급 받는다.")
                        .requestHeaders(headerWithName("Cookie")
                                .description("리프레시 토큰")));
    }

    public static RestDocumentationFilter AUTHORIZE_401_DOCUMENT() {
        return document(AUTH_API + "인가 실패 예시",
                new ResourceSnippetParametersBuilder().description("(인가 실패 예시) 지원자 권한으로 지원자 목록을 조회한다"));
    }
}
