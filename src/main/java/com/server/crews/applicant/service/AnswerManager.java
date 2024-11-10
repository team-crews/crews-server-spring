package com.server.crews.applicant.service;

import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.recruitment.domain.Question;
import jakarta.annotation.Nullable;

public abstract class AnswerManager<Q extends Question, A> {

    public A getValidatedAnswers(Q question, @Nullable A previousAnswer, @Nullable A newAnswer) {
        validateNecessity(question, newAnswer);
        if (newAnswer == null) {
            return null;
        }

        validate(question, newAnswer);
        return synchronizeWithPreviousAnswers(previousAnswer, newAnswer);
    }

    private void validateNecessity(Q question, A answer) {
//        if (question.isNecessary() && answer == null) {
//            throw new CrewsException(CrewsErrorCode.ANSWER_REQUIRED);
//        }
    }

    protected abstract void validate(Q question, A answer);

    protected abstract A synchronizeWithPreviousAnswers(@Nullable A previousAnswer, A newAnswer);
}
