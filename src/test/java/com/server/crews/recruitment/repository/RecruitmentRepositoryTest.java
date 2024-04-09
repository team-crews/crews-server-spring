package com.server.crews.recruitment.repository;

import com.server.crews.environ.RepositoryTest;
import com.server.crews.recruitment.domain.Recruitment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.server.crews.recruitment.application.RecruitmentFixture.DEFAULT_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecruitmentRepositoryTest extends RepositoryTest {
    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Test
    @DisplayName("지원서 양식 조회시 섹션 목록도 함께 조회한다.")
    void findDetailsById() {
        // given
        Long recruitmentId = saveDefaultRecruitment().getId();

        // when
        Recruitment recruitment = recruitmentRepository.findDetailsById(recruitmentId).get();

        // then
        assertAll(
                () -> assertThat(recruitment.getTitle()).isEqualTo(DEFAULT_TITLE),
                () -> assertThat(recruitment.getSections()).hasSize(2)
        );
    }
}
