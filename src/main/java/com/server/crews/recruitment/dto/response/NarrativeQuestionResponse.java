package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import lombok.Builder;

@Builder
public record NarrativeQuestionResponse(
        Long id,
        String content,
        Boolean necessity,
        Integer order,
        Integer wordLimit
) {

    public static NarrativeQuestionResponse from(final NarrativeQuestion question) {
        return NarrativeQuestionResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .necessity(question.getNecessity())
                .order(question.getOrder())
                .wordLimit(question.getWordLimit())
                .build();
    }
}
