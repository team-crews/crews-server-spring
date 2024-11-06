package com.server.crews.applicant.service;

import jakarta.annotation.Nullable;

public abstract class AnswerManager<Q, A> {

    public A getValidatedAnswers(Q question, A previousAnswer, A newAnswer) {
        validate(question, newAnswer);
        return synchronizeWithPreviousAnswers(previousAnswer, newAnswer);
    }

    protected abstract void validate(Q question, A newAnswer);

    protected abstract A synchronizeWithPreviousAnswers(@Nullable A previousAnswer, A newAnswer);
}
