package com.server.crews.recruitment.application;

import com.server.crews.environ.ServiceTest;
import com.server.crews.recruitment.domain.Progress;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.ProgressStateUpdateRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import com.server.crews.recruitment.dto.response.SelectiveQuestionResponse;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.server.crews.fixture.QuestionFixture.NARRATIVE_QUESTION;
import static com.server.crews.fixture.QuestionFixture.SELECTIVE_QUESTION;
import static com.server.crews.fixture.RecruitmentFixture.RECRUITMENT_SAVE_REQUEST;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecruitmentServiceTest extends ServiceTest {
    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Test
    @DisplayName("지원서 양식을 저장한다.")
    void saveRecruitment() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT().recruitment();

        // when
        recruitmentService.saveRecruitment(recruitment, RECRUITMENT_SAVE_REQUEST);

        // then
        Recruitment savedRecruitment = recruitmentRepository.findById(recruitment.getId()).get();
        assertThat(savedRecruitment.getTitle()).isEqualTo(RECRUITMENT_SAVE_REQUEST.getTitle());
    }

    @Test
    @DisplayName("지원서 양식의 진행 상태를 변경한다.")
    void updateProgressState() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT().recruitment();
        ProgressStateUpdateRequest request = new ProgressStateUpdateRequest(Progress.COMPLETION);

        // when
        recruitmentService.updateProgressState(recruitment, request);

        // then
        Recruitment updatedRecruitment = recruitmentRepository.findById(recruitment.getId()).get();
        assertThat(updatedRecruitment.getProgress()).isEqualTo(Progress.COMPLETION);
    }

    @Test
    @DisplayName("지원서 양식의 모든 상세정보를 조회한다.")
    void getRecruitmentDetails() {
        // given
        Long recruitmentId = LIKE_LION_RECRUITMENT()
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment()
                .getId();

        // when
        RecruitmentDetailsResponse response = recruitmentService.getRecruitmentDetails(recruitmentId);

        // then
        List<SectionResponse> sectionResponses = response.sections();
        assertAll(
                () -> assertThat(sectionResponses).hasSize(2),
                () -> assertThat(sectionResponses).extracting(SectionResponse::narrativeQuestions)
                        .hasSize(2),
                () -> assertThat(sectionResponses).extracting(SectionResponse::selectiveQuestions)
                        .hasSize(2),
                () -> assertThat(sectionResponses).flatExtracting(SectionResponse::selectiveQuestions)
                        .flatExtracting(SelectiveQuestionResponse::choices)
                        .hasSize(6)
        );
    }
}
