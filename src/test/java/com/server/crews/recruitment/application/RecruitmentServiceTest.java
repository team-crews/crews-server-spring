package com.server.crews.recruitment.application;

import com.server.crews.environ.IntegrationTest;
import com.server.crews.recruitment.domain.Progress;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.ProgressStateUpdateRequest;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.server.crews.recruitment.application.RecruitmentFixture.*;
import static com.server.crews.recruitment.application.SectionFixture.DEV_SECTIONS;
import static org.assertj.core.api.Assertions.assertThat;

class RecruitmentServiceTest extends IntegrationTest {
    @Autowired
    private RecruitmentService recruitmentService;

    @Test
    @DisplayName("지원서 양식을 저장한다.")
    void saveRecruitment() {
        // given
        Recruitment recruitment = integrationTestEnviron.saveLoginedRecruitment(DEFAULT_SECRET_CODE);

        // when
        recruitmentService.saveRecruitment(recruitment, RECRUITMENT_SAVE_REQUEST);

        // then
        Recruitment savedRecruitment = integrationTestEnviron.findById(recruitment.getId());
        assertThat(savedRecruitment.getTitle()).isEqualTo(RECRUITMENT_SAVE_REQUEST.getTitle());
    }

    @Test
    @DisplayName("지원서 양식의 진행 상태를 변경한다.")
    void updateProgressState() {
        // given
        Recruitment recruitment = integrationTestEnviron.saveDefaultRecruitment();

        ProgressStateUpdateRequest request = new ProgressStateUpdateRequest(Progress.COMPLETION);

        // when
        recruitmentService.updateProgressState(recruitment, request);

        // then
        Recruitment updatedRecruitment = integrationTestEnviron.findById(recruitment.getId());
        assertThat(updatedRecruitment.getProgress()).isEqualTo(Progress.COMPLETION);
    }
}
