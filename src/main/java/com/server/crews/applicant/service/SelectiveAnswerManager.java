package com.server.crews.applicant.service;

import static java.util.stream.Collectors.toMap;

import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SelectiveAnswerManager extends AnswerManager<SelectiveQuestion, List<SelectiveAnswer>> {

    @Override
    protected void validate(SelectiveQuestion question, List<SelectiveAnswer> answer) {
//        int selectionCount = answer.size();
//        if (question.isSelectionCountOutOfBounds(selectionCount)) {
//            throw new CrewsException(CrewsErrorCode.SELECTION_COUNT_OUT_OF_RANGE);
//        }
    }

    @Override
    protected List<SelectiveAnswer> synchronizeWithPreviousAnswers(@Nullable List<SelectiveAnswer> previousAnswer,
                                                                   List<SelectiveAnswer> newAnswer) {
        if (previousAnswer == null) {
            return newAnswer;
        }

        Map<Long, Long> previousAnswerIdsByChoiceId = previousAnswer.stream()
                .collect(toMap(SelectiveAnswer::getChoiceId, SelectiveAnswer::getId));
        newAnswer.forEach(selectiveAnswer ->
                selectiveAnswer.setOriginalId(previousAnswerIdsByChoiceId.get(selectiveAnswer.getChoiceId())));
        return newAnswer;
    }
}
