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
}
