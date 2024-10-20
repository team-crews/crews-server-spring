package com.server.crews.environ.repository;

import static com.server.crews.fixture.ApplicationFixture.APPLICATION;
import static com.server.crews.fixture.QuestionFixture.CHOICES;
import static com.server.crews.fixture.QuestionFixture.NARRATIVE_QUESTION;
import static com.server.crews.fixture.QuestionFixture.SELECTIVE_QUESTION;
import static com.server.crews.fixture.RecruitmentFixture.TEST_RECRUITMENT;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.SECTION;
import static com.server.crews.fixture.UserFixture.TEST_CLUB_NAME;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;

import com.server.crews.applicant.domain.Application;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.DatabaseCleaner;
import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(RepositoryTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryTest {
    @Autowired
    protected TestRepository testRepository;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
    }

    protected Administrator createDefaultAdmin() {
        Administrator administrator = new Administrator(TEST_CLUB_NAME, TEST_PASSWORD);
        testRepository.save(administrator);
        return administrator;
    }

    protected Applicant createDefaultApplicant(String email) {
        Applicant applicant = new Applicant(email, TEST_PASSWORD);
        testRepository.save(applicant);
        return applicant;
    }

    protected Recruitment createDefaultRecruitment(Administrator publisher) {
        Recruitment recruitment = TEST_RECRUITMENT(publisher);
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
        narrativeQuestion.updateRecruitment(section.getRecruitment());
        return narrativeQuestion;
    }

    private SelectiveQuestion createSelectiveQuestion(Section section) {
        SelectiveQuestion selectiveQuestion = SELECTIVE_QUESTION();
        selectiveQuestion.updateSection(section);
        selectiveQuestion.updateRecruitment(section.getRecruitment());
        return selectiveQuestion;
    }

    private List<Choice> createChoices(List<SelectiveQuestion> questions) {
        List<Choice> choices = CHOICES();
        questions.forEach(question -> choices.forEach(choice -> choice.updateSelectiveQuestion(question)));
        return choices;
    }

    protected Application createDefaultApplication(Long applicantId, Recruitment recruitment) {
        Application application = APPLICATION(applicantId, recruitment, List.of(), List.of());
        testRepository.save(application);
        return application;
    }
}
