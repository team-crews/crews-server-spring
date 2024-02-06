package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.Applicant;

public record ApplicantsResponse(String id, Long studentNumber, String name, String major) {
    public static ApplicantsResponse from(final Applicant applicant) {
        return new ApplicantsResponse(
                applicant.getId(), applicant.getStudentNumber(), applicant.getName(), applicant.getMajor());
    }
}
