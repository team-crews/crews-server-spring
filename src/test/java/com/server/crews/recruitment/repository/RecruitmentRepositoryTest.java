package com.server.crews.recruitment.repository;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.Recruitment;
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
                () -> assertThat(recruitment.getSections()).hasSize(2)
        );
    }
}
