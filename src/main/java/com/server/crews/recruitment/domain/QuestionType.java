package com.server.crews.recruitment.domain;

public enum QuestionType {
    NARRATIVE,
    SELECTIVE;

    public boolean hasSameName(String name) {
        return this.name().equals(name);
    }
}
