package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.Outcome;
import lombok.Builder;

@Builder
public record ApplicationsResponse(Long id, String studentNumber, String name, String major, Outcome outcome) {
    public static ApplicationsResponse from(final Application application) {
        return ApplicationsResponse.builder()
                .id(application.getId())
                .studentNumber(application.getStudentNumber())
                .name(application.getName())
                .major(application.getMajor())
                .outcome(application.getOutcome())
                .build();
    }
}
