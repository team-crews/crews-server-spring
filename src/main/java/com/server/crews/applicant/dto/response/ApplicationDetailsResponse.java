package com.server.crews.applicant.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ApplicationDetailsResponse(
        Long id,
        String studentNumber,
        String major,
        String name,
        List<AnswerResponse> answers
) {
}
