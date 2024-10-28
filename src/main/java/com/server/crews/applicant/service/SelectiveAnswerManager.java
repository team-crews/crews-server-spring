package com.server.crews.applicant.service;

import static java.util.stream.Collectors.toMap;

import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import java.util.List;
import java.util.Map;

public class SelectiveAnswerManager extends AnswerManager<SelectiveQuestion, List<SelectiveAnswer>> {
    public SelectiveAnswerManager(SelectiveQuestion question, List<SelectiveAnswer> previousAnswers,
                                  List<SelectiveAnswer> updatingAnswers) {
        super(question, previousAnswers, updatingAnswers);
    }

    @Override
    protected void validate() {
        // question 에 따라 검증
    }

    @Override
    protected List<SelectiveAnswer> synchronizeWithPreviousAnswers() {
        Map<Long, Long> previousAnswerIdsByChoiceId = previousAnswer.stream()
                .collect(toMap(SelectiveAnswer::getChoiceId, SelectiveAnswer::getId));
        newAnswer.forEach(selectiveAnswer ->
                selectiveAnswer.setOriginalId(previousAnswerIdsByChoiceId.get(selectiveAnswer.getChoiceId())));
        return newAnswer;
    }
}
