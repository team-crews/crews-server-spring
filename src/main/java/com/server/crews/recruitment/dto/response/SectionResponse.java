package com.server.crews.recruitment.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record SectionResponse(
        Long id,
        String name,
        String description,
        List<QuestionResponse> questions
) {
}
