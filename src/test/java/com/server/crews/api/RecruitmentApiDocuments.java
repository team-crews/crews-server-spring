package com.server.crews.api;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class RecruitmentApiDocuments {
    private static final String RECRUITMENT_API = "recruitment/";

    public static RestDocumentationFilter SAVE_RECRUITMENT_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 공고 저장",
                "모집 공고를 저장한다.",
                requestFields(
                        fieldWithPath(".id").description("모집 공고 id (최초 저장이 아닐 경우 필요함)").optional(),
                        fieldWithPath(".code").description("모집 공고 코드 (최초 저장이 아닐 경우 필요함)").optional(),
                        fieldWithPath(".title").description("모집 공고 제목"),
                        fieldWithPath(".description").description("모집 공고 설명").optional(),
                        fieldWithPath(".deadline").description("모집 마감일"),
                        fieldWithPath(".sections").description("섹션 목록 (없을 경우 빈 리스트)"),
                        fieldWithPath(".sections[].id").description("섹션 id (최초 저장이 아닐 경우 필요함)").optional(),
                        fieldWithPath(".sections[].name").description("섹션 이름"),
                        fieldWithPath(".sections[].description").description("섹션 설명").optional(),
                        fieldWithPath(".sections[].questions").description("질문 목록 (없을 경우 빈 리스트)"),
                        fieldWithPath(".sections[].questions[].id").description("질문 id (최초 저장이 아닐 경우 필요함)").optional(),
                        fieldWithPath(".sections[].questions[].type").description("질문 타입(NARRATIVE, SELECTIVE 중 하나)"),
                        fieldWithPath(".sections[].questions[].content").description("질문 내용"),
                        fieldWithPath(".sections[].questions[].necessity").description("필수여부"),
                        fieldWithPath(".sections[].questions[].order").description("질문순서 (오름차순으로 정렬됨)"),
                        fieldWithPath(".sections[].questions[].wordLimit").description("NARRATIVE 질문의 글자 수 제한")
                                .optional(),
                        fieldWithPath(".sections[].questions[].minimumSelection").description("SELECTIVE 질문의 최소 선택 개수")
                                .optional(),
                        fieldWithPath(".sections[].questions[].maximumSelection").description("SELECTIVE 질문의 최대 선택 개수")
                                .optional(),
                        fieldWithPath(".sections[].questions[].choices").description(
                                "SELECTIVE 질문의 선택지 목록 (없을 경우 빈 리스트)"),
                        fieldWithPath(".sections[].questions[].choices[].id").description("선택지 id (최초 저장이 아닐 경우 필요함)")
                                .optional(),
                        fieldWithPath(".sections[].questions[].choices[].content").description("선택지 내용")),
                recruitmentDetailsResponseFields());
    }

    public static RestDocumentationFilter SAVE_RECRUITMENT_400_DOCUMENT_WRONG_LETTER_LENGTH() {
        return document(RECRUITMENT_API + "모집 공고 저장 글자수 검증");
    }

    public static RestDocumentationFilter SAVE_RECRUITMENT_400_DOCUMENT_WRONG_NARRATIVE_QUESTION_WORD_LIMIT() {
        return document(RECRUITMENT_API + "모집 공고 저장 서술형 문항 최대 글자수 검증");
    }

    public static RestDocumentationFilter SAVE_RECRUITMENT_400_DOCUMENT_WRONG_SELECTIVE_QUESTION_SELECTION_COUNT() {
        return document(RECRUITMENT_API + "모집 공고 저장 선택형 문항 최소, 최대 선택 개수 검증");
    }

    public static RestDocumentationFilter SAVE_RECRUITMENT_400_DOCUMENT_INVALID_DEADLINE() {
        return document(RECRUITMENT_API + "잘못된 마감일의 모집 공고 저장",
                requestFields(
                        fieldWithPath(".id").description("모집 공고 id").optional(),
                        fieldWithPath(".code").description("모집 공고 코드").optional(),
                        fieldWithPath(".title").description("모집 공고 제목"),
                        fieldWithPath(".description").description("모집 공고 설명"),
                        fieldWithPath(".sections").description("섹션 목록"),
                        fieldWithPath(".deadline").description("현재 날짜 이후여야 하는 모집 마감일")));
    }

    public static RestDocumentationFilter SEARCH_RECRUITMENTS_TITLE_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 공고 제목 검색",
                "prefix로 모집 공고 제목 목록을 검색한다",
                queryParameters(
                        parameterWithName("prefix").description("접두사 (검색 키워드)"),
                        parameterWithName("limit").description("개수")),
                responseFields(fieldWithPath("[].title").description("모집 공고 제목")));
    }

    public static RestDocumentationFilter START_RECRUITMENT_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 시작",
                "모집을 시작한다.");
    }

    public static RestDocumentationFilter START_RECRUITMENT_409_DOCUMENT() {
        return document(RECRUITMENT_API + "유효하지 않은 모집 시작");
    }

    public static RestDocumentationFilter GET_RECRUITMENT_STATUS_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 중 지원 상태를 조회",
                "모집 중 지원 상태를 조회한다.",
                responseFields(
                        fieldWithPath("applicationCount").description("지원서 수"),
                        fieldWithPath("deadline").description("모집 마감 기한"),
                        fieldWithPath("code").description("모집 공고 코드")));
    }

    public static RestDocumentationFilter GET_READY_RECRUITMENT_200_DOCUMENT() {
        return document(RECRUITMENT_API + "작성중인 모집 공고(지원서 양식) 상세 조회",
                "작성중인 모집 공고(지원서 양식) 상세 정보를 조회한다.",
                recruitmentDetailsResponseFields());
    }

    public static RestDocumentationFilter GET_READY_RECRUITMENT_204_DOCUMENT() {
        return document(RECRUITMENT_API + "존재하지 않는 모집 공고(지원서 양식) 상세 조회");
    }

    public static RestDocumentationFilter GET_RECRUITMENT_BY_CODE_200_DOCUMENT() {
        return document(RECRUITMENT_API + "코드로 모집 공고 조회",
                "코드로 모집공고 상세 정보를 조회한다.",
                queryParameters(
                        parameterWithName("code").description("모집 공고 코드")),
                recruitmentDetailsResponseFields());
    }

    public static RestDocumentationFilter GET_RECRUITMENT_BY_CODE_409_DOCUMENT() {
        return document(RECRUITMENT_API + "준비 중인 모집 공고를 코드로 조회");
    }

    public static RestDocumentationFilter GET_RECRUITMENT_BY_TITLE_200_DOCUMENT() {
        return document(RECRUITMENT_API + "제목으로 모집 공고 조회",
                "제목으로 모집공고 상세 정보를 조회한다.",
                queryParameters(
                        parameterWithName("title").description("모집 공고 제목")),
                recruitmentDetailsResponseFields());
    }


    public static RestDocumentationFilter GET_RECRUITMENT_PROGRESS_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 공고 단계 조회",
                "모집공고의 단계를 조회한다.",
                responseFields(
                        fieldWithPath(".recruitmentProgress").description("모집 공고 단계(Progress)")));
    }

    public static RestDocumentationFilter UPDATE_RECRUITMENT_DEADLINE_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 마감일 변경",
                "모집 마감일을 변경한다.",
                requestFields(
                        fieldWithPath(".deadline").description("변경된 마감일")));
    }

    public static RestDocumentationFilter UPDATE_RECRUITMENT_DEADLINE_400_DOCUMENT() {
        return document(RECRUITMENT_API + "잘못된 모집 마감일 변경");
    }

    public static RestDocumentationFilter SEND_OUTCOME_EMAIL_200_REQUEST() {
        return document(RECRUITMENT_API + "지원 결과 메일 전송",
                "지원 결과 메일을 전송한다.");
    }

    public static RestDocumentationFilter SEND_OUTCOME_EMAIL_400_REQUEST() {
        return document(RECRUITMENT_API + "지원 결과 메일 재전송");
    }

    private static ResponseFieldsSnippet recruitmentDetailsResponseFields() {
        return responseFields(
                fieldWithPath(".id").description("모집 공고 id (최초 저장이 아닐 경우 필요함)"),
                fieldWithPath(".title").description("모집 공고 제목"),
                fieldWithPath(".description").description("모집 공고 설명").optional(),
                fieldWithPath(".deadline").description("모집 마감일"),
                fieldWithPath(".code").description("모집 공고 코드"),
                fieldWithPath(".recruitmentProgress").description("모집 공고 progress 상태"),
                fieldWithPath(".sections").description("섹션 목록 (없을 경우 빈 리스트)"),
                fieldWithPath(".sections[].id").description("섹션 id"),
                fieldWithPath(".sections[].name").description("섹션 이름"),
                fieldWithPath(".sections[].description").description("섹션 설명").optional(),
                fieldWithPath(".sections[].questions").description("질문 목록 (없을 경우 빈 리스트)"),
                fieldWithPath(".sections[].questions[].id").description("질문 id"),
                fieldWithPath(".sections[].questions[].type").description("질문 타입(NARRATIVE, SELECTIVE 중 하나)"),
                fieldWithPath(".sections[].questions[].content").description("질문 내용"),
                fieldWithPath(".sections[].questions[].necessity").description("필수여부"),
                fieldWithPath(".sections[].questions[].order").description("질문순서 (오름차순으로 정렬됨)"),
                fieldWithPath(".sections[].questions[].wordLimit").description("NARRATIVE 질문의 글자 수 제한")
                        .optional(),
                fieldWithPath(".sections[].questions[].minimumSelection").description("SELECTIVE 질문의 최소 선택 개수")
                        .optional(),
                fieldWithPath(".sections[].questions[].maximumSelection").description("SELECTIVE 질문의 최대 선택 개수")
                        .optional(),
                fieldWithPath(".sections[].questions[].choices").description(
                        "SELECTIVE 질문의 선택지 목록 (없을 경우 빈 리스트)"),
                fieldWithPath(".sections[].questions[].choices[].id").description("선택지 id"),
                fieldWithPath(".sections[].questions[].choices[].content").description("선택지 내용"));
    }
}
