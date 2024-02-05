package com.server.crews.recruitment.domain;

import java.util.List;
import lombok.Builder;

public class SelectiveQuestion extends Question {
    private Integer minimumSelection;

    private Integer maximumSelection;

    private List<String> options;

    @Builder
    public SelectiveQuestion(
            final String content, final Boolean necessity,
            final Integer order, final Integer minimumSelection,
            final Integer maximumSelection, final List<String> options) {
        this.content = content;
        this.necessity = necessity;
        this.order = order;
        this.minimumSelection = minimumSelection;
        this.maximumSelection = maximumSelection;
        this.options = options;
    }
}
