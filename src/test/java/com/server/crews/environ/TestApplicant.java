package com.server.crews.environ;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;

import java.util.ArrayList;
import java.util.List;

public class TestApplicant {
    private final TestEnviron environ;
    private Applicant applicant;
    private final List<NarrativeAnswer> narrativeAnswers;
    private final List<SelectiveAnswer> selectiveAnswers;

    public TestApplicant(final TestEnviron environ) {
        this.environ = environ;
        this.narrativeAnswers = new ArrayList<>();
        this.selectiveAnswers = new ArrayList<>();
    }

    public TestApplicant create(final String secretCode) {
        Applicant applicant = new Applicant(secretCode);
        this.applicant = environ.applicantRepository().save(applicant);
        return this;
    }

    public TestApplicant addNarrativeAnswers(final Long questionId, final String content) {
        NarrativeAnswer narrativeAnswer = NarrativeAnswer.builder()
                .narrativeQuestionId(questionId)
                .applicantId(applicant.getId())
                .content(content)
                .build();
        NarrativeAnswer savedNarrativeAnswer = environ.narrativeAnswerRepository().save(narrativeAnswer);
        this.narrativeAnswers.add(savedNarrativeAnswer);
        return this;
    }

    public TestApplicant saveSelectiveAnswers(final Long questionId, final Long choiceId) {
        SelectiveAnswer selectiveAnswer = SelectiveAnswer.builder()
                .selectiveQuestionId(questionId)
                .applicantId(applicant.getId())
                .choiceId(choiceId)
                .build();
        SelectiveAnswer savedSelectiveAnswer = environ.selectiveAnswerRepository().save(selectiveAnswer);
        this.selectiveAnswers.add(savedSelectiveAnswer);
        return this;
    }

    public Applicant applicant() {
        return applicant;
    }
}
