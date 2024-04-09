package com.server.crews.recruitment.application;

import com.server.crews.environ.IntegrationTest;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.server.crews.recruitment.application.RecruitmentFixture.RECRUITMENT_SAVE_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

class RecruitmentServiceTest extends IntegrationTest {
    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Test
    @DisplayName("지원서 양식을 저장한다.")
    void saveRecruitment() {
        // given
        Recruitment recruitment = new Recruitment("SECRET_CODE");
        recruitmentRepository.save(recruitment);

        // when
        recruitmentService.saveRecruitment(recruitment, RECRUITMENT_SAVE_REQUEST);

        // then
        Recruitment savedRecruitment = recruitmentRepository.findById(recruitment.getId()).get();
        assertThat(savedRecruitment.getTitle()).isEqualTo(RECRUITMENT_SAVE_REQUEST.getTitle());
    }

}
