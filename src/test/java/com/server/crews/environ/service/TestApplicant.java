package com.server.crews.environ.service;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;

import java.util.ArrayList;
import java.util.List;

import static com.server.crews.fixture.ApplicantFixture.DEFAULT_MAJOR;
import static com.server.crews.fixture.ApplicantFixture.DEFAULT_STUDENT_NUMBER;

public class TestApplicant {
    private final ServiceTestEnviron environ;
    private Applicant applicant;
    private final List<NarrativeAnswer> narrativeAnswers;
    private final List<SelectiveAnswer> selectiveAnswers;

    public TestApplicant(final ServiceTestEnviron environ) {
        this.environ = environ;
        this.narrativeAnswers = new ArrayList<>();
        this.selectiveAnswers = new ArrayList<>();
    }

    public TestApplicant create(final String secretCode, final Long recruitmentId) {
        Applicant applicant = new Applicant(secretCode, recruitmentId);
        this.applicant = environ.applicantRepository().save(applicant);
        return this;
    }

    public TestApplicant updateInformation(final String email, final String name) {
        ApplicationSaveRequest emailSaveRequest = new ApplicationSaveRequest(DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, email, name, null);
        this.applicant.updateAll(emailSaveRequest);
        environ.applicantRepository().save(this.applicant);
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

    public TestApplicant decideOutcome(final Outcome outcome) {
        applicant.decideOutcome(outcome);
        this.applicant = environ.applicantRepository().save(applicant);
        return this;
    }

    public Applicant applicant() {
        return applicant;
    }
}
