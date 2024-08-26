package com.server.crews.api;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class RecruitmentApiDocuments {
    private static final String RECRUITMENT_API = "recruitment/";

    public static RestDocumentationFilter SAVE_RECRUITMENT_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 공고 저장",
                new ResourceSnippetParametersBuilder().description("모집 공고를 저장한다.")
                        .requestFields(
                                fieldWithPath(".id")
                                        .description("모집 공고 id (최초 저장이 아닐 경우 필요함)")
                                        .optional(),
                                fieldWithPath(".title")
                                        .description("모집 공고 제목"),
                                fieldWithPath(".description")
                                        .description("모집 공고 설명")
                                        .optional(),
                                fieldWithPath(".deadline")
                                        .description("모집 마감일"),
                                fieldWithPath(".sections")
                                        .description("섹션 목록 (없을 경우 빈 리스트)"),
                                fieldWithPath(".sections[].id")
                                        .description("섹션 id (최초 저장이 아닐 경우 필요함)")
                                        .optional(),
                                fieldWithPath(".sections[].name")
                                        .description("섹션 이름"),
                                fieldWithPath(".sections[].description")
                                        .description("섹션 설명")
                                        .optional(),
                                fieldWithPath(".sections[].questions")
                                        .description("질문 목록 (없을 경우 빈 리스트)"),
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
                                        .description("SELECTIVE 질문의 선택지 목록 (없을 경우 빈 리스트)"),
                                fieldWithPath(".sections[].questions[].choices[].id")
                                        .description("선택지 id (최초 저장이 아닐 경우 필요함)")
                                        .optional(),
                                fieldWithPath(".sections[].questions[].choices[].content")
                                        .description("선택지 내용")));
    }

    public static RestDocumentationFilter SAVE_RECRUITMENT_400_DOCUMENT() {
        return document(RECRUITMENT_API + "잘못된 마감일의 모집 공고 저장",
                new ResourceSnippetParametersBuilder().description("잘못된 마감일로 모집 공고를 저장한다.")
                        .requestFields(
                                fieldWithPath(".id").description("모집 공고 id"),
                                fieldWithPath(".title").description("모집 공고 제목"),
                                fieldWithPath(".description").description("모집 공고 설명"),
                                fieldWithPath(".sections").description("섹션 목록"),
                                fieldWithPath(".deadline").description("현재 날짜 이후여야 하는 모집 마감일")));
    }

    public static RestDocumentationFilter START_RECRUITMENT_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 시작",
                new ResourceSnippetParametersBuilder().description("모집을 시작한다."));
    }

    public static RestDocumentationFilter START_RECRUITMENT_400_DOCUMENT() {
        return document(RECRUITMENT_API + "유효하지 않은 모집 시작",
                new ResourceSnippetParametersBuilder().description("이미 시작된 모집을 시작한다."));
    }

    public static RestDocumentationFilter GET_RECRUITMENT_STATUS_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 중 지원 상태를 조회",
                new ResourceSnippetParametersBuilder().description("모집 중 지원 상태를 조회한다."));
    }

    public static RestDocumentationFilter GET_RECRUITMENT_200_DOCUMENT() {
        return document(RECRUITMENT_API + "작성중인 모집 공고(지원서 양식) 상세 조회",
                new ResourceSnippetParametersBuilder().description("모집 공고(지원서 양식) 상세 정보를 조회한다."));
    }

    public static RestDocumentationFilter GET_RECRUITMENT_BY_CODE_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 공고 코드 확인",
                new ResourceSnippetParametersBuilder().description("코드로 모집공고 상세 정보를 조회한다.")
                        .queryParameters(parameterWithName("code").description("모집 공고 코드")));
    }

    public static RestDocumentationFilter UPDATE_RECRUITMENT_DEADLINE_200_DOCUMENT() {
        return document(RECRUITMENT_API + "모집 마감일 변경",
                new ResourceSnippetParametersBuilder().description("모집 마감일을 변경한다.")
                        .requestFields(fieldWithPath(".deadline").description("변경된 마감일")));
    }

    public static RestDocumentationFilter SEND_OUTCOME_EMAIL_200_REQUEST() {
        return document(RECRUITMENT_API + "지원 결과 메일 전송",
                new ResourceSnippetParametersBuilder().description("지원 결과 메일을 전송한다."));
    }
}
