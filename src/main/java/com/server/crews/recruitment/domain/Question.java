package com.server.crews.recruitment.domain;

public interface Question extends Comparable<Question> {
    Long getId();

    Integer getOrder();

    Long getSectionId();

    QuestionType getQuestionType();

    boolean isNecessary();

    @Override
    default int compareTo(Question o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}
