package com.server.crews.applicant.service;

import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class NarrativeAnswerManager extends AnswerManager<NarrativeQuestion, NarrativeAnswer> {

    @Override
    protected void validate(NarrativeQuestion question, NarrativeAnswer answer) {
        // question 에 따라 검증
    }

    @Override
    protected NarrativeAnswer synchronizeWithPreviousAnswers(@Nullable NarrativeAnswer previousAnswer,
                                                             NarrativeAnswer newAnswer) {
        if (previousAnswer == null) {
            return newAnswer;
        }

        newAnswer.setToOriginalId(previousAnswer.getId());
        return newAnswer;
    }
}
