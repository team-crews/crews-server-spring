package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.domain.Choice;

public record ChoiceSaveRequest(Long id, String content) {

    public Choice toEntity() {
        return new Choice(id, content);
    }
}
