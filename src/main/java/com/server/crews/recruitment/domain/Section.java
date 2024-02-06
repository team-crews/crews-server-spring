package com.server.crews.recruitment.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Section {
    private String name;
    private String description;
    private List<Question> questions;

    public void setQuestionOrder() {
        int sequence = 1;
        for(Question question: questions) {
            question.setOrder(sequence);
            sequence += 1;
        }
    }
}
