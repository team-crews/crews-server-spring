package com.server.crews.applicant.application;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApplicationForm {
    private List<NarrativeQuestion> narrativeQuestions;
    private List<SelectiveQuestion> selectiveQuestions;
    private Map<Long, NarrativeAnswer> previousNarrativeAnswersByQuestionId;
    private Map<Long, List<SelectiveAnswer>> previousSelectiveAnswersByQuestionId;

    public ApplicationForm(List<NarrativeQuestion> narrativeQuestions, List<SelectiveQuestion> selectiveQuestions) {
        this.narrativeQuestions = narrativeQuestions;
        this.selectiveQuestions = selectiveQuestions;
        this.previousNarrativeAnswersByQuestionId = new HashMap<>();
        this.previousSelectiveAnswersByQuestionId = new HashMap<>();
    }

    public ApplicationForm(List<NarrativeQuestion> narrativeQuestions, List<SelectiveQuestion> selectiveQuestions,
                           Application previousApplication) {
        this.narrativeQuestions = narrativeQuestions;
        this.selectiveQuestions = selectiveQuestions;
        this.previousNarrativeAnswersByQuestionId = previousApplication.getNarrativeAnswersByQuestionId();
        this.previousSelectiveAnswersByQuestionId = previousApplication.getSelectiveAnswersByQuestionId();
    }

    public List<NarrativeAnswer> writeNarrativeAnswers(List<NarrativeAnswer> newNarrativeAnswers) {
        Map<Long, NarrativeAnswer> newNarrativeAnswersByQuestionId = newNarrativeAnswers.stream()
                .collect(toMap(NarrativeAnswer::getQuestionId, identity()));

        return narrativeQuestions.stream()
                .map(question -> writerNarrativeAnswer(question, newNarrativeAnswersByQuestionId.get(question.getId())))
                .filter(Objects::nonNull)
                .toList();
    }

    private NarrativeAnswer writerNarrativeAnswer(NarrativeQuestion question, NarrativeAnswer newAnswer) {
        if (newAnswer == null) {
            return null;
        }

        NarrativeAnswer previousAnswer = previousNarrativeAnswersByQuestionId.get(question.getId());
        NarrativeAnswerManager answerManager = new NarrativeAnswerManager(question, previousAnswer, newAnswer);
        return answerManager.getValidatedAnswers();
    }

    public List<SelectiveAnswer> writeSelectiveAnswers(List<SelectiveAnswer> newSelectiveAnswers) {
        Map<Long, List<SelectiveAnswer>> newSelectiveAnswersByQuestionId = newSelectiveAnswers.stream()
                .collect(groupingBy(SelectiveAnswer::getQuestionId));

        return selectiveQuestions.stream()
                .map(question -> writeSelectiveAnswers(question, newSelectiveAnswersByQuestionId.get(question.getId())))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<SelectiveAnswer> writeSelectiveAnswers(SelectiveQuestion question,
                                                        List<SelectiveAnswer> newAnswers) {
        if (newAnswers == null) {
            return null;
        }

        List<SelectiveAnswer> previousAnswers = previousSelectiveAnswersByQuestionId.getOrDefault(question.getId(),
                List.of());
        SelectiveAnswerManager answerManager = new SelectiveAnswerManager(question, previousAnswers, newAnswers);
        return answerManager.getValidatedAnswers();
    }
}
