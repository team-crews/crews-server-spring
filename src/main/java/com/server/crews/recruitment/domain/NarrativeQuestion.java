package com.server.crews.recruitment.domain;

import lombok.Builder;

public class NarrativeQuestion extends Question {
    private Integer wordLimit;

    @Builder
    public NarrativeQuestion(
            final String content, final Boolean necessity,
            final Integer order, final Integer wordLimit) {
        this.content = content;
        this.necessity = necessity;
        this.order = order;
        this.wordLimit = wordLimit;
    }
}
