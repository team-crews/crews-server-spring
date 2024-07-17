package com.server.crews.environ.service;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;

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

    public TestRecruitment(ServiceTestEnviron environ) {
        this.environ = environ;
        this.sections = new ArrayList<>();
        this.narrativeQuestions = new ArrayList<>();
        this.selectiveQuestions = new ArrayList<>();
        this.choices = new ArrayList<>();
    }

    public TestRecruitment create(String code, String clubName, Administrator publisher) {
        Recruitment recruitment = new Recruitment(code, clubName + " 99기 모집", DEFAULT_DESCRIPTION,
                DEFAULT_CLOSING_DATE, publisher, List.of());
        this.recruitment = environ.recruitmentRepository().save(recruitment);
        return this;
    }

    public TestRecruitment addSection(String name, List<NarrativeQuestion> narrativeQuestions,
                                      List<SelectiveQuestion> selectiveQuestions) {
        Section section = new Section(name, DEFAULT_DESCRIPTION, narrativeQuestions, selectiveQuestions);
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

    private List<Choice> choicesInSelectiveQuestions(List<SelectiveQuestion> selectiveQuestions) {
        return selectiveQuestions.stream()
                .map(SelectiveQuestion::getChoices)
                .flatMap(List::stream)
                .toList();
    }

    public Recruitment recruitment() {
        return this.recruitment;
    }

    public List<NarrativeQuestion> narrativeQuestions() {
        return this.narrativeQuestions;
    }

    public List<SelectiveQuestion> selectiveQuestions() {
        return this.selectiveQuestions;
    }

    public List<Choice> choices(int questionIndex) {
        return this.selectiveQuestions.get(questionIndex).getChoices();
    }
}
