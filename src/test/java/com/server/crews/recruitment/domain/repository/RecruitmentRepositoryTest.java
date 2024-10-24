package com.server.crews.recruitment.domain.repository;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DEADLINE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.Recruitment;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RecruitmentRepositoryTest extends RepositoryTest {
    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Test
    @DisplayName("작성자 id로 지원서 양식 조회시 섹션 목록도 함께 조회한다.")
    void findWithSectionsByPublisherId() {
        // given
        Administrator publisher = createDefaultAdmin();
        createDefaultRecruitment(publisher);

        // when
        Recruitment recruitment = recruitmentRepository.findWithSectionsByPublisherId(publisher.getId()).get();

        // then
        assertAll(
                () -> assertThat(recruitment.getTitle()).isEqualTo(DEFAULT_TITLE),
                () -> assertThat(recruitment.getOrderedSections()).hasSize(2)
        );
    }

    @Test
    @DisplayName("모집 공고 제목은 30자 이내이다.")
    void saveWithValidation() {
        // given
        Administrator publisher = createDefaultAdmin();
        Recruitment recruitment = new Recruitment(null, DEFAULT_CODE, "DEFAULT_TITLE_DEFAULT_TITLE_31_",
                DEFAULT_DESCRIPTION, DEFAULT_DEADLINE, publisher, List.of());

        // when & then
        assertThatThrownBy(() -> recruitmentRepository.save(recruitment))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
