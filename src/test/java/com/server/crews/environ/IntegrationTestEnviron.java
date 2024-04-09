package com.server.crews.environ;

import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.recruitment.domain.*;
import com.server.crews.recruitment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.server.crews.recruitment.application.QuestionFixture.*;
import static com.server.crews.recruitment.application.RecruitmentFixture.RECRUITMENT;
import static com.server.crews.recruitment.application.SectionFixture.DEV_SECTIONS;

@Component
@RequiredArgsConstructor
public class IntegrationTestEnviron {
    private final ApplicantRepository applicantRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final SectionRepository sectionRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final ChoiceRepository choiceRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public Recruitment saveDefaultRecruitment() {
        return saveRecruitment(
                RECRUITMENT,
                DEV_SECTIONS,
                List.of(NARRATIVE_QUESTION),
                List.of(SELECTIVE_QUESTION),
                CHOICES
        );
    }

    public Recruitment saveLoginedRecruitment(final String secretCode) {
        return saveRecruitment(
                new Recruitment(secretCode),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public Recruitment saveRecruitment(
            final Recruitment recruitment,
            final List<Section> sections,
            final List<NarrativeQuestion> narrativeQuestions,
            final List<SelectiveQuestion> selectiveQuestions,
            final List<Choice> choices) {
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        List<Section> savedSections = saveSections(sections, recruitment);
        List<SelectiveQuestion> savedSelectiveQuestions = saveSelectiveQuestions(selectiveQuestions, savedSections);
        saveChoices(choices, savedSelectiveQuestions);
        saveNarrativeQuestions(narrativeQuestions, savedSections);
        return savedRecruitment;
    }

    public List<Section> saveSections(final List<Section> sections, final Recruitment recruitment) {
        sections.forEach(section -> section.updateRecruitment(recruitment));
        return sectionRepository.saveAll(sections);
    }

    public List<NarrativeQuestion> saveNarrativeQuestions(final List<NarrativeQuestion> questions, final List<Section> sections) {
        sections.forEach(section ->
                questions.forEach(question -> question.updateSection(section)));
        return narrativeQuestionRepository.saveAll(questions);
    }

    public List<SelectiveQuestion> saveSelectiveQuestions(final List<SelectiveQuestion> questions, final List<Section> sections) {
        sections.forEach(section ->
                questions.forEach(question -> question.updateSection(section)));
        return selectiveQuestionRepository.saveAll(questions);
    }

    public List<Choice> saveChoices(final List<Choice> choices, final List<SelectiveQuestion> questions) {
        questions.forEach(question ->
                choices.forEach(choice -> choice.updateSelectiveQuestion(question)));
        return choiceRepository.saveAll(choices);
    }

    public Recruitment findById(final Long id) {
        return recruitmentRepository.findById(id).get();
    }
}
