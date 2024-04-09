package com.server.crews.recruitment.application;

import com.server.crews.recruitment.dto.request.QuestionRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionRequest;

import java.time.LocalDateTime;
import java.util.List;

public class RecruitmentFixture {
    public static final QuestionRequest NARRATIVE_QUESTION_REQUEST = new QuestionRequest(QuestionType.NARRATIVE, "자기소개해주세요", true, 1, 100, null, null, null);
    public static final QuestionRequest SELECTIVE_QUESTION_REQUEST = new QuestionRequest(QuestionType.SELECTIVE, "장점을 골라주세요", true, 2, null, 1, 2, List.of("성실함", "밝음", "꼼꼼함"));
    public static final List<QuestionRequest> QUESTION_REQUESTS = List.of(NARRATIVE_QUESTION_REQUEST, SELECTIVE_QUESTION_REQUEST);
    public static final SectionRequest BACKEND_SECTION_REQUEST = new SectionRequest("BACKEND", "description", QUESTION_REQUESTS);
    public static final SectionRequest FRONTEND_SECTION_REQUEST = new SectionRequest("FRONTEND", "description", QUESTION_REQUESTS);
    public static final List<SectionRequest> SECTION_REQUESTS = List.of(BACKEND_SECTION_REQUEST, FRONTEND_SECTION_REQUEST);
    public static final RecruitmentSaveRequest RECRUITMENT_SAVE_REQUEST = new RecruitmentSaveRequest("title", "clubName", "description", SECTION_REQUESTS, LocalDateTime.now().plusMonths(1L));
}
