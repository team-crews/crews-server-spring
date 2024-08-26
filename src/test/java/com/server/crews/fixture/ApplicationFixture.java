package com.server.crews.fixture;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.List;

public class ApplicationFixture {
    public static final String DEFAULT_STUDENT_NUMBER = "DEFAULT_STUDENT_NUMBER";
    public static final String DEFAULT_MAJOR = "DEFAULT_MAJOR";
    public static final String DEFAULT_NAME = "DEFAULT_NAME";
    public static final String DEFAULT_NARRATIVE_ANSWER = "DEFAULT_NARRATIVE_ANSWER";

    public static Application APPLICATION(Applicant applicant, Recruitment recruitment,
                                          List<NarrativeAnswer> narrativeAnswers,
                                          List<SelectiveAnswer> selectiveAnswers) {
        return new Application(null, recruitment, applicant, DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, DEFAULT_NAME,
                narrativeAnswers, selectiveAnswers);
    }
}
