package com.server.crews.applicant.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Answer {
    private Integer order;
    private String content;
    private List<String> choices;

    public void setOrder(int order) {
        this.order = order;
    }
}
