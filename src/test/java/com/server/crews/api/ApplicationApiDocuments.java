package com.server.crews.api;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class ApplicationApiDocuments {
    private static final String APPLICATION_API = "application/";

    public static RestDocumentationFilter SAVE_APPLICATION_200_DOCUMENT() {
        return document(APPLICATION_API + "지원서 저장",
                "지원서를 저장한다.",
                requestFields(
                        fieldWithPath(".id").description("지원서 id (최초 저장이 아닐 경우 필요함)").optional(),
                        fieldWithPath(".recruitmentCode").description("모집 공고 코드"),
                        fieldWithPath(".studentNumber").description("학번"),
                        fieldWithPath(".major").description("전공"),
                        fieldWithPath(".name").description("지원자 이름"),
                        fieldWithPath(".sections").description("섹션 목록"),
                        fieldWithPath(".sections[].sectionId").description("섹션 id"),
                        fieldWithPath(".sections[].answers[]").description("섹션의 답안 목록"),
                        fieldWithPath(".sections[].answers[].questionType").description("질문 타입(NARRATIVE, SELECTIVE 중 하나)"),
                        fieldWithPath(".sections[].answers[].questionId").description("질문 id"),
                        fieldWithPath(".sections[].answers[].content").description("서술형 답안 (NARRATIVE 질문의 경우)").optional(),
                        fieldWithPath(".sections[].answers[].choiceIds").description("선택지 id 목록 (SELECTIVE 질문의 경우)").optional()),
                applicationDetailsResponseFields());
    }

    public static RestDocumentationFilter SAVE_APPLICATION_NOT_STARTED_409_DOCUMENT() {
        return document(APPLICATION_API + "모집이 시작되지 않은 모집 공고로 지원서 저장");
    }

    public static RestDocumentationFilter SAVE_APPLICATION_CLOSED_RECRUITMENT_409_DOCUMENT() {
        return document(APPLICATION_API + "모집이 종료된 모집 공고로 지원서 저장");
    }

    public static RestDocumentationFilter SAVE_APPLICATION_404_DOCUMENT() {
        return document(APPLICATION_API + "존재하지 않는 질문으로 지원서 저장");
    }

    public static RestDocumentationFilter GET_APPLICATION_200_DOCUMENT() {
        return document(APPLICATION_API + "동아리 관리자 지원서 상세 조회",
                "동아리 관리자가 지원서 상세 정보를 조회한다.",
                pathParameters(
                        parameterWithName("application-id").description("지원서 id")),
                applicationDetailsResponseFields());
    }

    public static RestDocumentationFilter GET_MY_APPLICATION_200_DOCUMENT() {
        return document(APPLICATION_API + "지원자 지원서 상세 조회",
                "지원자가 본인의 지원서 상세 정보를 조회한다.",
                queryParameters(parameterWithName("code").description("모집공고 code")),
                applicationDetailsResponseFields());
    }

    public static RestDocumentationFilter GET_MY_APPLICATION_204_DOCUMENT() {
        return document(APPLICATION_API + "지원자 존재하지 않는 지원서 상세 조회");
    }

    public static RestDocumentationFilter GET_APPLICATIONS_200_DOCUMENT() {
        return document(APPLICATION_API + "지원서 목록 조회",
                "지원서 목록을 조회한다.",
                responseFields(
                        fieldWithPath("[].id").description("지원서 id"),
                        fieldWithPath("[].studentNumber").description("학번"),
                        fieldWithPath("[].name").description("지원자 이름"),
                        fieldWithPath("[].major").description("전공"),
                        fieldWithPath("[].outcome").description("지원 결과 (PENDING, PASS, FAIL 중 하나)")));
    }

    public static RestDocumentationFilter EVALUATE_APPLICATIONS_200_DOCUMENT() {
        return document(APPLICATION_API + "지원서 평가",
                "지원서를 평가를 저장한다.",
                requestFields(
                        fieldWithPath(".passApplicationIds").description("합격 지원서 id 목록")));
    }

    public static RestDocumentationFilter EVALUATE_APPLICATIONS_409_DOCUMENT() {
        return document(APPLICATION_API + "평가 및 결과 발표 완료된 모집 공고의 지원서 평가");
    }

    private static ResponseFieldsSnippet applicationDetailsResponseFields() {
        return responseFields(
                fieldWithPath(".id").description("지원서 id"),
                fieldWithPath(".studentNumber").description("학번"),
                fieldWithPath(".major").description("전공"),
                fieldWithPath(".name").description("지원자 이름"),
                fieldWithPath(".sections").description("섹션 목록"),
                fieldWithPath(".sections[].sectionId").description("섹션 id"),
                fieldWithPath(".sections[].answers").description("답변 목록"),
                fieldWithPath(".sections[].answers[].type").description("답변 타입(NARRATIVE, SELECTIVE 중 하나)"),
                fieldWithPath(".sections[].answers[].choiceIds").description("선택지 id 리스트 (SELECTIVE 질문의 경우)").optional(),
                fieldWithPath(".sections[].answers[].content").description("서술형 답안 (NARRATIVE 질문의 경우)").optional(),
                fieldWithPath(".sections[].answers[].questionId").description("질문 id"));
    }
}
