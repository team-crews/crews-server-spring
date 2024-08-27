package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.Outcome;
import lombok.Builder;

@Builder
public record ApplicationsResponse(
        Long id,
        String studentNumber,
        String name,
        String major,
        Outcome outcome
) {
}
