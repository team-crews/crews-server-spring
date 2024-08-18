package com.server.crews.api;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class ApplicationApiDocuments {
    private static final String RECRUITMENT_API = "application/";

    public static RestDocumentationFilter GET_APPLICATION_200_DOCUMENT() {
        return document(RECRUITMENT_API + "지원서 상세 조회",
                pathParameters(parameterWithName("application-id").description("지원서 id")));
    }
}
