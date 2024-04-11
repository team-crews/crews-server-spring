package com.server.crews.fixture;

import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionsSaveRequest;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.crews.fixture.QuestionFixture.*;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;

public class RecruitmentFixture {
    public static final LocalDateTime DEFAULT_DEADLINE = LocalDateTime.now().plusMonths(1L);
    public static final String DEFAULT_SECRET_CODE = "SECRET_CODE";
    public static final String DEFAULT_TITLE = "TITLE";
    public static final String DEFAULT_CLUB_NAME = "CLUB_NAME";
    public static final String DEFAULT_DESCRIPTION = "DESCRIPTION";

    public static final QuestionSaveRequest NARRATIVE_QUESTION_REQUEST = new QuestionSaveRequest(QuestionType.NARRATIVE, INTRODUCTION_QUESTION, true, 1, 100, null, null, null);
    public static final QuestionSaveRequest SELECTIVE_QUESTION_REQUEST = new QuestionSaveRequest(QuestionType.SELECTIVE, STRENGTH_QUESTION, true, 2, null, 1, 2, STRENGTH_CHOICES);
    public static final List<QuestionSaveRequest> QUESTION_REQUESTS = List.of(NARRATIVE_QUESTION_REQUEST, SELECTIVE_QUESTION_REQUEST);
    public static final SectionsSaveRequest BACKEND_SECTION_REQUEST = SectionsSaveRequest.builder()
            .questions(QUESTION_REQUESTS)
            .name(BACKEND_SECTION_NAME)
            .description(DEFAULT_DESCRIPTION)
            .build();
    public static final SectionsSaveRequest FRONTEND_SECTION_REQUEST = SectionsSaveRequest.builder()
            .questions(QUESTION_REQUESTS)
            .name(FRONTEND_SECTION_NAME)
            .description(DEFAULT_DESCRIPTION)
            .build();
    public static final List<SectionsSaveRequest> SECTION_REQUESTS = List.of(BACKEND_SECTION_REQUEST, FRONTEND_SECTION_REQUEST);
    public static final RecruitmentSaveRequest RECRUITMENT_SAVE_REQUEST = new RecruitmentSaveRequest(DEFAULT_TITLE, DEFAULT_CLUB_NAME, DEFAULT_DESCRIPTION, SECTION_REQUESTS, DEFAULT_DEADLINE);

    public static Recruitment RECRUITMENT() {
        return Recruitment.builder()
                .secretCode(DEFAULT_SECRET_CODE)
                .title(DEFAULT_TITLE)
                .clubName(DEFAULT_CLUB_NAME)
                .description(DEFAULT_DESCRIPTION)
                .deadline(DEFAULT_DEADLINE)
                .build();
    }
}
