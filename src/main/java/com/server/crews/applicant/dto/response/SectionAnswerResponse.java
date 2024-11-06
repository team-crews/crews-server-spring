package com.server.crews.applicant.dto.response;

import java.util.List;

public record SectionAnswerResponse(
        Long sectionId,
        List<AnswerResponse> answers
) {
}
