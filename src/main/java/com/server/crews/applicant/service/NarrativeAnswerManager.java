package com.server.crews.applicant.service;

import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class NarrativeAnswerManager extends AnswerManager<NarrativeQuestion, NarrativeAnswer> {

    @Override
    protected void validate(NarrativeQuestion question, NarrativeAnswer answer) {
        String content = answer.getContent();
        if (question.exceedWordLimit(content.length())) {
            throw new CrewsException(CrewsErrorCode.EXCEED_WORD_LIMIT_ANSWER);
        }
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
