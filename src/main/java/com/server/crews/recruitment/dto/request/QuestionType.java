package com.server.crews.recruitment.dto.request;

public enum QuestionType {
    NARRATIVE,
    SELECTIVE;

    public boolean hasSameName(String name) {
        return this.name().equals(name);
    }
}
