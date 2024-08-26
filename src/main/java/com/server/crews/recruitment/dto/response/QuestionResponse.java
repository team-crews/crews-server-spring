package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.dto.request.QuestionType;
import java.util.List;
import lombok.Builder;

@Builder
public record QuestionResponse(
        Long id,
        QuestionType type,
        String content,
        Boolean necessity,
        Integer order,
        Integer wordLimit,
        Integer minimumSelection,
        Integer maximumSelection,
        List<ChoiceResponse> choices
) {
}
