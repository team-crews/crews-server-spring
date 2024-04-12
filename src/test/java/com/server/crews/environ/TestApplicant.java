package com.server.crews.environ;

import com.server.crews.applicant.domain.Applicant;

public class TestApplicant {
    private final TestEnviron environ;
    private Applicant applicant;

    public TestApplicant(final TestEnviron environ) {
        this.environ = environ;
    }

    public TestApplicant create(final String secretCode) {
        Applicant applicant = new Applicant(secretCode);
        this.applicant = environ.applicantRepository().save(applicant);
        return this;
    }

    public Applicant applicant() {
        return applicant;
    }
}
