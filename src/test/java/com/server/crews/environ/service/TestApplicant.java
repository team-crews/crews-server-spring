package com.server.crews.environ.service;

import com.server.crews.auth.domain.Applicant;

public class TestApplicant {
    private final ServiceTestEnviron environ;
    private Applicant applicant;

    public TestApplicant(ServiceTestEnviron environ) {
        this.environ = environ;
    }

    public TestApplicant create(String email, String password) {
        String encodedPassword = environ.passwordEncoder().encode(password);
        Applicant applicant = new Applicant(email, encodedPassword);
        this.applicant = environ.applicantRepository().save(applicant);
        return this;
    }

    public Applicant applicant() {
        return applicant;
    }
}
