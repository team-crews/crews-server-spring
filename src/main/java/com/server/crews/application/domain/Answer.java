package com.server.crews.application.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Answer {
    private Integer order;
    private String content;
    private List<String> choices;
}
