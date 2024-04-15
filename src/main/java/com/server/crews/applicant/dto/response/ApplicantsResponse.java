package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.Outcome;
import lombok.Builder;

@Builder
public record ApplicantsResponse(Long id, String studentNumber, String name, String major, Outcome outcome) {
    public static ApplicantsResponse from(final Applicant applicant) {
        return ApplicantsResponse.builder()
                .id(applicant.getId())
                .studentNumber(applicant.getStudentNumber())
                .name(applicant.getName())
                .major(applicant.getMajor())
                .outcome(applicant.getOutcome())
                .build();
    }
}
