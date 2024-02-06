package com.server.crews.recruitment.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Question {
    private QuestionType type;
    private String content;
    private Boolean necessity;
    private Integer order;
    private Integer wordLimit;
    private Integer minimumSelection;
    private Integer maximumSelection;
    private List<String> options;
}
