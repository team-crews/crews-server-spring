package com.server.crews.recruitment.repository;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NarrativeQuestionRepositoryTest extends RepositoryTest {
    @Autowired
    private NarrativeQuestionRepository narrativeQuestionRepository;

    @Test
    @DisplayName("섹션들의 모든 서술형 문항을 조회한다.")
    void findAllBySectionIn() {
        // given
        Administrator publisher = createDefaultAdmin();
        Recruitment recruitment = saveDefaultRecruitment(publisher);
        List<Section> sections = recruitment.getSections();

        // when
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllBySectionIn(sections);

        // then
        assertThat(narrativeQuestions).hasSize(2);
    }
}
