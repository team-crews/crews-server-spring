package com.server.crews.fixture;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionsSaveRequest;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.crews.fixture.QuestionFixture.INTRODUCTION_QUESTION;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_CHOICES;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_QUESTION;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;

public class RecruitmentFixture {
    public static final LocalDateTime DEFAULT_CLOSING_DATE = LocalDateTime.now().plusMonths(1L);
    public static final String DEFAULT_CODE = "SECRET_CODE";
    public static final String DEFAULT_TITLE = "TITLE";
    public static final String DEFAULT_DESCRIPTION = "DESCRIPTION";

    public static final QuestionSaveRequest NARRATIVE_QUESTION_REQUEST = new QuestionSaveRequest(QuestionType.NARRATIVE.name(), INTRODUCTION_QUESTION, true, 1, 100, null, null, null);
    public static final QuestionSaveRequest SELECTIVE_QUESTION_REQUEST = new QuestionSaveRequest(QuestionType.SELECTIVE.name(), STRENGTH_QUESTION, true, 2, null, 1, 2, STRENGTH_CHOICES);
    public static final List<QuestionSaveRequest> QUESTION_REQUESTS = List.of(NARRATIVE_QUESTION_REQUEST, SELECTIVE_QUESTION_REQUEST);
    public static final SectionsSaveRequest BACKEND_SECTION_REQUEST = new SectionsSaveRequest(BACKEND_SECTION_NAME, DEFAULT_DESCRIPTION, QUESTION_REQUESTS);
    public static final SectionsSaveRequest FRONTEND_SECTION_REQUEST = new SectionsSaveRequest(FRONTEND_SECTION_NAME, DEFAULT_DESCRIPTION, QUESTION_REQUESTS);
    public static final List<SectionsSaveRequest> SECTION_REQUESTS = List.of(BACKEND_SECTION_REQUEST, FRONTEND_SECTION_REQUEST);
    public static final RecruitmentSaveRequest RECRUITMENT_SAVE_REQUEST = new RecruitmentSaveRequest(DEFAULT_TITLE, DEFAULT_DESCRIPTION, SECTION_REQUESTS, DEFAULT_CLOSING_DATE);

    public static Recruitment TEST_RECRUITMENT(Administrator publisher) {
        return new Recruitment(DEFAULT_CODE, DEFAULT_TITLE, DEFAULT_DESCRIPTION, DEFAULT_CLOSING_DATE, publisher, List.of());
    }
}
