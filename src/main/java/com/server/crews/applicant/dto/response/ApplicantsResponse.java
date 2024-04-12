package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.Outcome;

public record ApplicantsResponse(Long id, String studentNumber, String name, String major, Outcome outcome) {
    public static ApplicantsResponse from(final Applicant applicant) {
        return new ApplicantsResponse(
                applicant.getId(), applicant.getStudentNumber(),
                applicant.getName(), applicant.getMajor(), applicant.getOutcome());
    }
}
