package com.server.crews.recruitment.application;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.environ.service.ServiceTest;
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
    void createRecruitment() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();

        // when
        RecruitmentDetailsResponse response = recruitmentService.createRecruitment(publisher.getId(), RECRUITMENT_SAVE_REQUEST);

        // then
        assertThat(response.id()).isNotNull();
    }

    @Test
    @DisplayName("지원서 양식의 진행 상태를 변경한다.")
    void updateProgressState() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher).recruitment();
        ProgressStateUpdateRequest request = new ProgressStateUpdateRequest(Progress.COMPLETION);

        // when
        recruitmentService.updateProgressState(recruitment.getId(), request);

        // then
        Recruitment updatedRecruitment = recruitmentRepository.findById(recruitment.getId()).get();
        assertThat(updatedRecruitment.getProgress()).isEqualTo(Progress.COMPLETION);
    }

    @Test
    @DisplayName("지원서 양식의 모든 상세정보를 조회한다.")
    void findRecruitmentDetails() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Long recruitmentId = LIKE_LION_RECRUITMENT(publisher)
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment()
                .getId();

        // when
        RecruitmentDetailsResponse response = recruitmentService.findRecruitmentDetails(recruitmentId);

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
