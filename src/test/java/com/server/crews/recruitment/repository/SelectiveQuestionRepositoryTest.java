package com.server.crews.recruitment.repository;

import static com.server.crews.fixture.QuestionFixture.CHOICES;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_QUESTION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SelectiveQuestionRepositoryTest extends RepositoryTest {
    @Autowired
    private SelectiveQuestionRepository selectiveQuestionRepository;

    @Test
    @DisplayName("선택형 문항의 최소, 최대 선택 개수는 1이상 10이하의 값이다.")
    void saveWithValidation() {
        // given
        Administrator publisher = createDefaultAdmin();
        Recruitment recruitment = createDefaultRecruitment(publisher);
        List<Section> sections = recruitment.getOrderedSections();

        SelectiveQuestion selectiveQuestion = new SelectiveQuestion(null, CHOICES(), STRENGTH_QUESTION, true, 1, 11,
                11);
        selectiveQuestion.updateSection(sections.get(0));

        // when & then
        assertThatThrownBy(() -> selectiveQuestionRepository.save(selectiveQuestion))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
