package com.server.crews.recruitment.dto.response;

import com.server.crews.recruitment.domain.Choice;

public record ChoiceResponse(Long id, String content) {

    public static ChoiceResponse from(final Choice choice) {
        return new ChoiceResponse(choice.getId(), choice.getContent());
    }
}
