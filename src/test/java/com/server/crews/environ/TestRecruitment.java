package com.server.crews.environ;

import com.server.crews.recruitment.domain.*;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;

import java.util.ArrayList;
import java.util.List;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DEADLINE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;

public class TestRecruitment {
    private final TestEnviron environ;
    private final List<Section> sections;
    private final List<NarrativeQuestion> narrativeQuestions;
    private final List<SelectiveQuestion> selectiveQuestions;
    private final List<Choice> choices;
    private Recruitment recruitment;

    public TestRecruitment(final TestEnviron environ) {
        this.environ = environ;
        this.sections = new ArrayList<>();
        this.narrativeQuestions = new ArrayList<>();
        this.selectiveQuestions = new ArrayList<>();
        this.choices = new ArrayList<>();
    }

    public TestRecruitment create(final String secretCode, final String clubName) {
        Recruitment recruitment = new Recruitment(secretCode);
        recruitment.updateAll(new RecruitmentSaveRequest(clubName + " 99기 모집", clubName, DEFAULT_DESCRIPTION, null, DEFAULT_DEADLINE));
        this.recruitment = environ.recruitmentRepository().save(recruitment);
        return this;
    }

    public TestRecruitment addSection(
            final String name,
            final List<NarrativeQuestion> narrativeQuestions,
            final List<SelectiveQuestion> selectiveQuestions) {
        Section section = Section.builder()
                .name(name)
                .description(DEFAULT_DESCRIPTION)
                .narrativeQuestions(narrativeQuestions)
                .selectiveQuestions(selectiveQuestions)
                .build();
        section.updateRecruitment(this.recruitment);
        Section savedSection = environ.sectionRepository().save(section);
        List<NarrativeQuestion> savedNarrativeQuestions = environ.narrativeQuestionRepository().saveAll(narrativeQuestions);
        List<SelectiveQuestion> savedSelectiveQuestions = environ.selectiveQuestionRepository().saveAll(selectiveQuestions);
        List<Choice> choices = choicesInSelectiveQuestions(selectiveQuestions);
        List<Choice> savedChoices = environ.choiceRepository().saveAll(choices);
        this.sections.add(savedSection);
        this.narrativeQuestions.addAll(savedNarrativeQuestions);
        this.selectiveQuestions.addAll(savedSelectiveQuestions);
        this.choices.addAll(savedChoices);
        return this;
    }

    private List<Choice> choicesInSelectiveQuestions(final List<SelectiveQuestion> selectiveQuestions) {
        return selectiveQuestions.stream()
                .map(SelectiveQuestion::getChoices)
                .flatMap(List::stream)
                .toList();
    }

    public Recruitment recruitment() {
        return this.recruitment;
    }
}
