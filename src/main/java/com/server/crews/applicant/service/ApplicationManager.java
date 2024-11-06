package com.server.crews.applicant.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationManager {

    private final NarrativeAnswerManager narrativeAnswerManager;
    private final SelectiveAnswerManager selectiveAnswerManager;

    public List<NarrativeAnswer> writeNarrativeAnswers(Recruitment recruitment,
                                                       @Nullable Application previousApplication,
                                                       List<NarrativeAnswer> newNarrativeAnswers) {
        List<NarrativeQuestion> narrativeQuestions = recruitment.getNarrativeQuestion();
        Map<Long, NarrativeAnswer> newNarrativeAnswersByQuestionId = newNarrativeAnswers.stream()
                .collect(toMap(NarrativeAnswer::getQuestionId, identity()));

        if (previousApplication == null) {
            return writeNarrativeAnswersWithQuestions(narrativeQuestions, new HashMap<>(),
                    newNarrativeAnswersByQuestionId);
        }

        Map<Long, NarrativeAnswer> previousNarrativeAnswersByQuestionId = previousApplication.getNarrativeAnswersByQuestionId();
        return writeNarrativeAnswersWithQuestions(narrativeQuestions, previousNarrativeAnswersByQuestionId,
                newNarrativeAnswersByQuestionId);
    }

    private List<NarrativeAnswer> writeNarrativeAnswersWithQuestions(List<NarrativeQuestion> narrativeQuestions,
                                                                     Map<Long, NarrativeAnswer> previousNarrativeAnswersByQuestionId,
                                                                     Map<Long, NarrativeAnswer> newNarrativeAnswersByQuestionId) {
        return narrativeQuestions.stream()
                .map(question -> narrativeAnswerManager.getValidatedAnswers(
                        question,
                        previousNarrativeAnswersByQuestionId.get(question.getId()),
                        newNarrativeAnswersByQuestionId.get(question.getId()))
                )
                .filter(Objects::nonNull)
                .toList();
    }

    public List<SelectiveAnswer> writeSelectiveAnswers(Recruitment recruitment,
                                                       @Nullable Application previousApplication,
                                                       List<SelectiveAnswer> newSelectiveAnswers) {
        List<SelectiveQuestion> selectiveQuestions = recruitment.getSelectiveQuestions();
        Map<Long, List<SelectiveAnswer>> newSelectiveAnswersByQuestionId = newSelectiveAnswers.stream()
                .collect(groupingBy(SelectiveAnswer::getQuestionId));

        if (previousApplication == null) {
            return writeSelectiveAnswersWithQuestionId(selectiveQuestions, new HashMap<>(),
                    newSelectiveAnswersByQuestionId);
        }

        Map<Long, List<SelectiveAnswer>> previousSelectiveAnswersByQuestionId = previousApplication.getSelectiveAnswersByQuestionId();
        return writeSelectiveAnswersWithQuestionId(selectiveQuestions, previousSelectiveAnswersByQuestionId,
                newSelectiveAnswersByQuestionId);
    }

    private List<SelectiveAnswer> writeSelectiveAnswersWithQuestionId(List<SelectiveQuestion> selectiveQuestions,
                                                                      Map<Long, List<SelectiveAnswer>> previousSelectiveAnswersByQuestionId,
                                                                      Map<Long, List<SelectiveAnswer>> newSelectiveAnswersByQuestionId) {
        return selectiveQuestions.stream()
                .map(question -> selectiveAnswerManager.getValidatedAnswers(
                        question,
                        previousSelectiveAnswersByQuestionId.get(question.getId()),
                        newSelectiveAnswersByQuestionId.get(question.getId()))
                )
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }
}
