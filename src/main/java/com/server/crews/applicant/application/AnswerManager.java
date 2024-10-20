package com.server.crews.applicant.application;

public abstract class AnswerManager<Q, A> {
    protected Q question;
    protected A previousAnswer;
    protected A newAnswer;

    public AnswerManager(Q question, A previousAnswer, A newAnswer) {
        this.question = question;
        this.previousAnswer = previousAnswer;
        this.newAnswer = newAnswer;
    }

    public A getValidatedAnswers() {
        validate();
        return synchronizeWithPreviousAnswers();
    }

   protected abstract void validate();

    protected abstract A synchronizeWithPreviousAnswers();
}
