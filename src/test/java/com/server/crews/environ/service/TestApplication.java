package com.server.crews.environ.service;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.SelectiveQuestion;
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

    public TestApplication create(Applicant applicant, Recruitment recruitment, String studentNumber, String major,
                                  String name) {
        Application application = new Application(null, recruitment, applicant.getId(), studentNumber, major, name, List.of(),
                List.of());
        this.application = environ.applicationRepository().save(application);
        return this;
    }

    public TestApplication addNarrativeAnswers(NarrativeQuestion question, String content) {
        NarrativeAnswer narrativeAnswer = new NarrativeAnswer(null, question, content);
        narrativeAnswer.updateApplication(this.application);
        NarrativeAnswer savedNarrativeAnswer = environ.narrativeAnswerRepository().save(narrativeAnswer);
        this.application.replaceNarrativeAnswers(List.of(narrativeAnswer));
        this.narrativeAnswers.add(savedNarrativeAnswer);
        return this;
    }

    public TestApplication saveSelectiveAnswers(SelectiveQuestion question, Choice choice) {
        SelectiveAnswer selectiveAnswer = new SelectiveAnswer(null, choice, question);
        selectiveAnswer.updateApplication(this.application);
        SelectiveAnswer savedSelectiveAnswer = environ.selectiveAnswerRepository().save(selectiveAnswer);
        this.application.replaceSelectiveAnswers(List.of(selectiveAnswer));
        this.selectiveAnswers.add(savedSelectiveAnswer);
        return this;
    }

    public TestApplication pass() {
        application.pass();
        this.application = environ.applicationRepository().save(application);
        return this;
    }

    public Application application() {
        return application;
    }
}
