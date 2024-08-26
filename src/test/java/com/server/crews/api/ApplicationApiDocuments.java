package com.server.crews.api;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class ApplicationApiDocuments {
    private static final String APPLICATION_API = "application/";

    public static RestDocumentationFilter SAVE_APPLICATION_200_DOCUMENT() {
        return document(APPLICATION_API + "지원서 저장",
                new ResourceSnippetParametersBuilder().description("지원서를 저장한다.")
                        .requestFields(
                                fieldWithPath(".id")
                                        .description("지원서 id (최초 저장이 아닐 경우 필요함)")
                                        .optional(),
                                fieldWithPath(".studentNumber")
                                        .description("학번"),
                                fieldWithPath(".major")
                                        .description("전공"),
                                fieldWithPath(".name")
                                        .description("이름"),
                                fieldWithPath(".answers")
                                        .description("답변 목록")
                                        .optional(),
                                fieldWithPath(".answers[].answerId")
                                        .description("답변 id")
                                        .optional(),
                                fieldWithPath(".answers[].questionType")
                                        .description("질문 타입(NARRATIVE, SELECTIVE 중 하나)"),
                                fieldWithPath(".answers[].questionId")
                                        .description("질문 id"),
                                fieldWithPath(".answers[].content")
                                        .description("서술형 답안 (NARRATIVE 질문의 경우)")
                                        .optional(),
                                fieldWithPath(".answers[].choiceId")
                                        .description("선택지 id (SELECTIVE 질문의 경우)")
                                        .optional()));
    }

    public static RestDocumentationFilter SAVE_APPLICATION_404_DOCUMENT() {
        return document(APPLICATION_API + "존재하지 않는 질문으로 지원서 저장");
    }

    public static RestDocumentationFilter SAVE_APPLICATION_400_DOCUMENT() {
        return document(APPLICATION_API + "한 서술형 문항에 두 개 이상의 답변으로 지원서 저장");
    }

    public static RestDocumentationFilter GET_APPLICATION_200_DOCUMENT() {
        return document(APPLICATION_API + "동아리 관리자 지원서 상세 조회",
                new ResourceSnippetParametersBuilder().description("동아리 관리자가 지원서 상세 정보를 조회한다.")
                        .pathParameters(parameterWithName("application-id").description("지원서 id")));
    }

    public static RestDocumentationFilter GET_APPLICATIONS_200_DOCUMENT() {
        return document(APPLICATION_API + "지원서 목록 조회",
                new ResourceSnippetParametersBuilder().description("지원서 목록을 조회한다."));
    }

    public static RestDocumentationFilter EVALUATE_APPLICATIONS_200_DOCUMENT() {
        return document(APPLICATION_API + "지원서 평가",
                new ResourceSnippetParametersBuilder().description("지원서를 평가한다.")
                        .requestFields(fieldWithPath(".passApplicationIds").description("합격 지원서 id 목록")));
    }
}
