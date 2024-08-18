package com.server.crews.api;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class AdministratorApiDocuments {
    private static final String RECRUITMENT_API = "recruitment/";

    public static RestDocumentationFilter SAVE_RECRUITMENT_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 공고 저장",
                SAVE_RECRUITMENT_200_REQUEST_FIELDS());
    }

    public static RequestFieldsSnippet SAVE_RECRUITMENT_200_REQUEST_FIELDS() {
        return requestFields(
                fieldWithPath(".id")
                        .description("모집 공고 id")
                        .optional(),
                fieldWithPath(".title")
                        .description("모집 공고 제목"),
                fieldWithPath(".description")
                        .description("모집 공고 설명")
                        .optional(),
                fieldWithPath(".closingDate")
                        .description("모집 마감일"),
                fieldWithPath(".sections")
                        .description("섹션 목록")
                        .optional(),
                fieldWithPath(".sections[].id")
                        .description("섹션 id (최초 저장이 아닐 경우 필요함)")
                        .optional(),
                fieldWithPath(".sections[].name")
                        .description("섹션 이름"),
                fieldWithPath(".sections[].description")
                        .description("섹션 설명")
                        .optional(),
                fieldWithPath(".sections[].questions")
                        .description("질문 목록")
                        .optional(),
                fieldWithPath(".sections[].questions[].id")
                        .description("질문 id (최초 저장이 아닐 경우 필요함)")
                        .optional(),
                fieldWithPath(".sections[].questions[].type")
                        .description("질문 타입(NARRATIVE, SELECTIVE 중 하나)"),
                fieldWithPath(".sections[].questions[].content")
                        .description("질문 내용"),
                fieldWithPath(".sections[].questions[].necessity")
                        .description("필수여부"),
                fieldWithPath(".sections[].questions[].order")
                        .description("질문순서 (오름차순으로 정렬됨)"),
                fieldWithPath(".sections[].questions[].wordLimit")
                        .description("NARRATIVE 질문의 글자 수 제한")
                        .optional(),
                fieldWithPath(".sections[].questions[].minimumSelection")
                        .description(
                                "SELECTIVE 질문의 최소 선택 개수")
                        .optional(),
                fieldWithPath(".sections[].questions[].maximumSelection")
                        .description("SELECTIVE 질문의 최대 선택 개수")
                        .optional(),
                fieldWithPath(".sections[].questions[].choices")
                        .description("SELECTIVE 질문의 선택지 목록")
                        .optional(),
                fieldWithPath(".sections[].questions[].choices[].id")
                        .description("선택지 id (최초 저장이 아닐 경우 필요함)")
                        .optional(),
                fieldWithPath(".sections[].questions[].choices[].content")
                        .description("선택지 내용"));
    }

    public static RestDocumentationFilter SAVE_RECRUITMENT_400_DOCUMENT() {
        return document(RECRUITMENT_API + "잘못된 마감일의 모집 공고 저장",
                requestFields(
                        fieldWithPath(".id").description("모집 공고 id"),
                        fieldWithPath(".title").description("모집 공고 제목"),
                        fieldWithPath(".description").description("모집 공고 설명"),
                        fieldWithPath(".sections").description("섹션 목록"),
                        fieldWithPath(".closingDate").description("현재 날짜 이후여야 하는 모집 마감일")));
    }

    public static RestDocumentationFilter START_RECRUITMENT_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 시작");
    }

    public static RestDocumentationFilter START_RECRUITMENT_400_DOCUMENT() {
        return document(RECRUITMENT_API + "유효하지 않은 모집 시작");
    }

    public static RestDocumentationFilter GET_RECRUITMENT_STATUS_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 중 지원 상태를 조회");
    }

    public static RestDocumentationFilter GET_RECRUITMENT_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 공고(지원서 양식) 상세 조회",
                pathParameters(parameterWithName("recruitment-id").description("모집 공고 id")));
    }

    public static RestDocumentationFilter UPDATE_RECRUITMENT_CLOSING_DATE_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 마감일 변경",
                requestFields(fieldWithPath(".closingDate").description("변경된 마감일")));
    }

    public static RestDocumentationFilter SEND_OUTCOME_EMAIL_200_REQUEST() {
        return document(RECRUITMENT_API + "지원 결과 메일 전송");
    }

    public static RestDocumentationFilter GET_APPLICATIONS_200_DOCUMENT() {
        return document(RECRUITMENT_API + "지원서 목록 조회");
    }
}
