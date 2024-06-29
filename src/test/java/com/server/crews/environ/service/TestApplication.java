package com.server.crews.environ.service;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.auth.domain.Applicant;

import java.util.ArrayList;
import java.util.List;

public class TestApplication {
    private final ServiceTestEnviron environ;
    private Application application;
    private final List<NarrativeAnswer> narrativeAnswers;
    private final List<SelectiveAnswer> selectiveAnswers;

    public TestApplication(ServiceTestEnviron environ) {
        this.environ = environ;
        this.narrativeAnswers = new ArrayList<>();
        this.selectiveAnswers = new ArrayList<>();
    }

    public TestApplication create(Applicant applicant, String studentNumber, String major, String name) {
        Application application = new Application(applicant, studentNumber, major, name);
        this.application = environ.applicationRepository().save(application);
        return this;
    }

    public TestApplication addNarrativeAnswers(Long questionId, String content) {
        NarrativeAnswer narrativeAnswer = NarrativeAnswer.builder()
                .narrativeQuestionId(questionId)
                .applicantId(application.getId())
                .content(content)
                .build();
        NarrativeAnswer savedNarrativeAnswer = environ.narrativeAnswerRepository().save(narrativeAnswer);
        this.narrativeAnswers.add(savedNarrativeAnswer);
        return this;
    }

    public TestApplication saveSelectiveAnswers(Long questionId, Long choiceId) {
        SelectiveAnswer selectiveAnswer = SelectiveAnswer.builder()
                .selectiveQuestionId(questionId)
                .applicantId(application.getId())
                .choiceId(choiceId)
                .build();
        SelectiveAnswer savedSelectiveAnswer = environ.selectiveAnswerRepository().save(selectiveAnswer);
        this.selectiveAnswers.add(savedSelectiveAnswer);
        return this;
    }

    public TestApplication decideOutcome(Outcome outcome) {
        application.decideOutcome(outcome);
        this.application = environ.applicationRepository().save(application);
        return this;
    }

    public Application application() {
        return application;
    }
}
