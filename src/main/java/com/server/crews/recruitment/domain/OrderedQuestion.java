package com.server.crews.recruitment.domain;

public interface OrderedQuestion extends Comparable<OrderedQuestion> {
    Long getId();

    Integer getOrder();

    Long getSectionId();

    QuestionType getQuestionType();

    @Override
    default int compareTo(OrderedQuestion o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}
