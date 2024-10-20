package com.server.crews.applicant.application;

import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.recruitment.domain.NarrativeQuestion;

public class NarrativeAnswerManager extends AnswerManager<NarrativeQuestion, NarrativeAnswer> {
    public NarrativeAnswerManager(NarrativeQuestion question, NarrativeAnswer previousAnswer,
                                  NarrativeAnswer newAnswer) {
        super(question, previousAnswer, newAnswer);
    }

    @Override
    protected void validate() {
        // question 에 따라 검증
    }

    @Override
    protected NarrativeAnswer synchronizeWithPreviousAnswers() {
        if (previousAnswer != null) {
            newAnswer.setToOriginalId(previousAnswer.getId());
        }
        return newAnswer;
    }
}
