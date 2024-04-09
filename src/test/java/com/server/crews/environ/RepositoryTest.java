package com.server.crews.environ;

import com.server.crews.recruitment.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.server.crews.recruitment.application.QuestionFixture.*;
import static com.server.crews.recruitment.application.RecruitmentFixture.RECRUITMENT;
import static com.server.crews.recruitment.application.SectionFixture.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryTest {
    @Autowired
    protected TestRepository testRepository;

    protected Recruitment saveDefaultRecruitment() {
        Recruitment recruitment = RECRUITMENT();
        Section BESection = createSection(BACKEND_SECTION_NAME, recruitment);
        Section FESection = createSection(FRONTEND_SECTION_NAME, recruitment);
        NarrativeQuestion BENarrativeQuestion = createNarrativeQuestion(BESection);
        NarrativeQuestion FENarrativeQuestion = createNarrativeQuestion(FESection);
        SelectiveQuestion BESelectivequestion = createSelectiveQuestion(BESection);
        SelectiveQuestion FESelectivequestion = createSelectiveQuestion(FESection);
        createChoices(List.of(BESelectivequestion, FESelectivequestion));
        testRepository.save(
                recruitment, BESection, FESection, BENarrativeQuestion, FENarrativeQuestion,
                BESelectivequestion, FESelectivequestion
        );
        return recruitment;
    }

    private Section createSection(String sectionName, Recruitment recruitment) {
        Section section = SECTION(sectionName);
        section.updateRecruitment(recruitment);
        recruitment.addSections(List.of(section));
        return section;
    }

    private NarrativeQuestion createNarrativeQuestion(Section section) {
        NarrativeQuestion narrativeQuestion = NARRATIVE_QUESTION();
        narrativeQuestion.updateSection(section);
        return narrativeQuestion;
    }

    private SelectiveQuestion createSelectiveQuestion(Section section) {
        SelectiveQuestion selectiveQuestion = SELECTIVE_QUESTION();
        selectiveQuestion.updateSection(section);
        return selectiveQuestion;
    }

    private List<Choice> createChoices(List<SelectiveQuestion> questions) {
        List<Choice> choices = CHOICES();
        questions.forEach(question -> choices.forEach(choice -> choice.updateSelectiveQuestion(question)));
        return choices;
    }
}
