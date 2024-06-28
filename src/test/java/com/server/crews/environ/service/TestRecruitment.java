package com.server.crews.environ.service;

import com.server.crews.recruitment.domain.*;

import java.util.ArrayList;
import java.util.List;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CLOSING_DATE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;

public class TestRecruitment {
    private final ServiceTestEnviron environ;
    private final List<Section> sections;
    private final List<NarrativeQuestion> narrativeQuestions;
    private final List<SelectiveQuestion> selectiveQuestions;
    private final List<Choice> choices;
    private Recruitment recruitment;

    public TestRecruitment(final ServiceTestEnviron environ) {
        this.environ = environ;
        this.sections = new ArrayList<>();
        this.narrativeQuestions = new ArrayList<>();
        this.selectiveQuestions = new ArrayList<>();
        this.choices = new ArrayList<>();
    }

    public TestRecruitment create(final String code, final String clubName) {
        Recruitment recruitment = new Recruitment(code);
        recruitment.updateAll(clubName + " 99기 모집", DEFAULT_DESCRIPTION, DEFAULT_CLOSING_DATE, List.of());
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
