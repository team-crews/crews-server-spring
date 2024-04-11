package com.server.crews.fixture;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;

public class ApplicantFixture {
    public static final String DEFAULT_SECRET_CODE = "SECRET_CODE";
    public static final String DEFAULT_STUDENT_NUMBER = "DEFAULT_STUDENT_NUMBER";
    public static final String DEFAULT_MAJOR = "DEFAULT_MAJOR";
    public static final String DEFAULT_EMAIL = "DEFAULTEMAIL@gmail.com";
    public static final String DEFAULT_NAME = "DEFAULT_NAME";

    public static final String DEFAULT_NARRATIVE_ANSWER = "DEFAULT_NARRATIVE_ANSWER";

    public static Applicant APPLICANT() {
        return new Applicant(DEFAULT_SECRET_CODE);
    }
}
