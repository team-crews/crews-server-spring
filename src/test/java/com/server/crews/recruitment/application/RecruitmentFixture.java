package com.server.crews.recruitment.application;

import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.QuestionRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionRequest;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.crews.recruitment.application.QuestionFixture.*;
import static com.server.crews.recruitment.application.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.recruitment.application.SectionFixture.FRONTEND_SECTION_NAME;

public class RecruitmentFixture {
    public static final LocalDateTime DEFAULT_DEADLINE = LocalDateTime.now().plusMonths(1L);
    public static final String DEFAULT_SECRET_CODE = "SECRET_CODE";
    public static final String DEFAULT_TITLE = "TITLE";
    public static final String DEFAULT_CLUB_NAME = "CLUB_NAME";
    public static final String DEFAULT_DESCRIPTION = "DESCRIPTION";

    public static final QuestionRequest NARRATIVE_QUESTION_REQUEST = new QuestionRequest(QuestionType.NARRATIVE, INTRODUCTION_QUESTION, true, 1, 100, null, null, null);
    public static final QuestionRequest SELECTIVE_QUESTION_REQUEST = new QuestionRequest(QuestionType.SELECTIVE, STRENGTH_QUESTION, true, 2, null, 1, 2, STRENGTH_CHOICES);
    public static final List<QuestionRequest> QUESTION_REQUESTS = List.of(NARRATIVE_QUESTION_REQUEST, SELECTIVE_QUESTION_REQUEST);
    public static final SectionRequest BACKEND_SECTION_REQUEST = new SectionRequest(BACKEND_SECTION_NAME, DEFAULT_DESCRIPTION, QUESTION_REQUESTS);
    public static final SectionRequest FRONTEND_SECTION_REQUEST = new SectionRequest(FRONTEND_SECTION_NAME, DEFAULT_DESCRIPTION, QUESTION_REQUESTS);
    public static final List<SectionRequest> SECTION_REQUESTS = List.of(BACKEND_SECTION_REQUEST, FRONTEND_SECTION_REQUEST);
    public static final RecruitmentSaveRequest RECRUITMENT_SAVE_REQUEST = new RecruitmentSaveRequest(DEFAULT_TITLE, DEFAULT_CLUB_NAME, DEFAULT_DESCRIPTION, SECTION_REQUESTS, DEFAULT_DEADLINE);

    public static final Recruitment RECRUITMENT = Recruitment.builder()
            .secretCode(DEFAULT_SECRET_CODE)
            .title(DEFAULT_TITLE)
            .clubName(DEFAULT_CLUB_NAME)
            .description(DEFAULT_DESCRIPTION)
            .deadline(DEFAULT_DEADLINE)
            .build();
}
