package com.server.crews.fixture;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionSaveRequest;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.crews.fixture.QuestionFixture.INTRODUCTION_QUESTION;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_CHOICES_REQUEST;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_QUESTION;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;

public class RecruitmentFixture {
    public static final LocalDateTime DEFAULT_DEADLINE = LocalDateTime.of(2030, 9, 5, 18, 0);
    public static final String DEFAULT_CODE = "SECRET_CODE";
    public static final String DEFAULT_TITLE = "TITLE";
    public static final String DEFAULT_DESCRIPTION = "DESCRIPTION";

    public static final QuestionSaveRequest NARRATIVE_QUESTION_REQUEST = new QuestionSaveRequest(null, QuestionType.NARRATIVE.name(), INTRODUCTION_QUESTION, true, 1, 100, null, null, List.of());
    public static final QuestionSaveRequest SELECTIVE_QUESTION_REQUEST = new QuestionSaveRequest(null, QuestionType.SELECTIVE.name(), STRENGTH_QUESTION, true, 2, null, 1, 2, STRENGTH_CHOICES_REQUEST);
    public static final List<QuestionSaveRequest> QUESTION_REQUESTS = List.of(NARRATIVE_QUESTION_REQUEST, SELECTIVE_QUESTION_REQUEST);
    public static final SectionSaveRequest BACKEND_SECTION_REQUEST = new SectionSaveRequest(null, BACKEND_SECTION_NAME, DEFAULT_DESCRIPTION, QUESTION_REQUESTS);
    public static final SectionSaveRequest FRONTEND_SECTION_REQUEST = new SectionSaveRequest(null, FRONTEND_SECTION_NAME, DEFAULT_DESCRIPTION, QUESTION_REQUESTS);
    public static final List<SectionSaveRequest> SECTION_REQUESTS = List.of(BACKEND_SECTION_REQUEST, FRONTEND_SECTION_REQUEST);
    public static final RecruitmentSaveRequest RECRUITMENT_SAVE_REQUEST = new RecruitmentSaveRequest(null, DEFAULT_TITLE, DEFAULT_DESCRIPTION, SECTION_REQUESTS, DEFAULT_DEADLINE.toString());

    public static Recruitment TEST_RECRUITMENT(Administrator publisher) {
        return new Recruitment(null, DEFAULT_CODE, DEFAULT_TITLE, DEFAULT_DESCRIPTION, DEFAULT_DEADLINE, publisher, List.of());
    }
}
