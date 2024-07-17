package com.server.crews.environ.service;

import com.server.crews.auth.domain.Applicant;
import com.server.crews.recruitment.domain.Recruitment;

public class TestApplicant {
    private final ServiceTestEnviron environ;
    private Applicant applicant;

    public TestApplicant(ServiceTestEnviron environ) {
        this.environ = environ;
    }

    public TestApplicant create(String email, String password, Recruitment recruitment) {
        Applicant applicant = new Applicant(email, password, recruitment);
        this.applicant = environ.applicantRepository().save(applicant);
        return this;
    }

    public Applicant applicant() {
        return applicant;
    }
}
