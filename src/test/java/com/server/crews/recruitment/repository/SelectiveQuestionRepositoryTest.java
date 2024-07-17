package com.server.crews.recruitment.repository;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SelectiveQuestionRepositoryTest extends RepositoryTest {
    @Autowired
    private SelectiveQuestionRepository selectiveQuestionRepository;

    @Test
    @DisplayName("선택형 문항을 조회할 때 선택지들도 함께 조회한다.")
    void findAllWithChoicesBySection() {
        // given
        Administrator publisher = createDefaultAdmin();
        Recruitment recruitment = saveDefaultRecruitment(publisher);

        // when
        List<SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllWithChoicesInSections(recruitment.getSections());

        // then
        assertAll(
                () -> assertThat(selectiveQuestions).hasSize(2),
                () -> assertThat(selectiveQuestions).flatExtracting(SelectiveQuestion::getChoices)
                        .hasSize(6)
        );
    }
}
