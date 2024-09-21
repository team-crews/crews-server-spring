package com.server.crews.recruitment.repository;

import static com.server.crews.fixture.QuestionFixture.INTRODUCTION_QUESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NarrativeQuestionRepositoryTest extends RepositoryTest {
    @Autowired
    private NarrativeQuestionRepository narrativeQuestionRepository;

    @Test
    @DisplayName("섹션들의 모든 서술형 문항을 조회한다.")
    void findAllBySectionIn() {
        // given
        Administrator publisher = createDefaultAdmin();
        Recruitment recruitment = createDefaultRecruitment(publisher);
        List<Section> sections = recruitment.getSections();

        // when
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllBySectionIn(sections);

        // then
        assertThat(narrativeQuestions).hasSize(2);
    }

    @Test
    @DisplayName("글자 수 제한은 최대 1500까지 가능하다.")
    void saveWithValidation() {
        // given
        Administrator publisher = createDefaultAdmin();
        Recruitment recruitment = createDefaultRecruitment(publisher);
        List<Section> sections = recruitment.getSections();

        NarrativeQuestion narrativeQuestion = new NarrativeQuestion(null, INTRODUCTION_QUESTION, true, 1, 1501);
        narrativeQuestion.updateSection(sections.get(0));

        // when & then
        assertThatThrownBy(() -> narrativeQuestionRepository.save(narrativeQuestion))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
