package com.server.crews.recruitment.application;

import static com.server.crews.fixture.QuestionFixture.NARRATIVE_QUESTION;
import static com.server.crews.fixture.QuestionFixture.SELECTIVE_QUESTION;
import static com.server.crews.fixture.RecruitmentFixture.RECRUITMENT_SAVE_REQUEST;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.service.ServiceTest;
import com.server.crews.recruitment.domain.Progress;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import com.server.crews.recruitment.dto.response.SelectiveQuestionResponse;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class RecruitmentServiceTest extends ServiceTest {
    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    ApplicationEvents events;

    @Test
    @DisplayName("지원서 양식을 최초로 저장한다.")
    void createRecruitment() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();

        // when
        RecruitmentDetailsResponse response = recruitmentService.saveRecruitment(publisher.getId(),
                RECRUITMENT_SAVE_REQUEST);

        // then
        assertThat(response.id()).isNotNull();
    }

    @Test
    @DisplayName("지원서 양식을 수정 저장한다.")
    void updateRecruitment() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher).recruitment();
        LocalDateTime modifiedClosingDateTime = LocalDateTime.now().plusDays(1L);
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(recruitment.getTitle(),
                recruitment.getDescription(), List.of(), modifiedClosingDateTime.toString());

        // when
        RecruitmentDetailsResponse response = recruitmentService.saveRecruitment(publisher.getId(),
                recruitmentSaveRequest);

        // then
        assertAll(() -> {
            assertThat(response.id()).isEqualTo(recruitment.getId());
            assertThat(response.closingDate()).isEqualTo(modifiedClosingDateTime);
        });
    }

    @Test
    @DisplayName("모집을 시작한다.")
    void startRecruiting() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher).recruitment();

        // when
        recruitmentService.startRecruiting(publisher.getId());

        // then
        Recruitment updatedRecruitment = recruitmentRepository.findById(recruitment.getId()).get();
        assertThat(updatedRecruitment.getProgress()).isEqualTo(Progress.IN_PROGRESS);
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

    @Test
    @DisplayName("지원 결과를 저장하고 지원 결과 이메일을 전송한다.")
    void sendOutcomeEmail() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher)
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment();
        Applicant jongmee = JONGMEE_APPLICANT(recruitment).applicant();
        Application jongmeeApplication = JONGMEE_APPLICATION(jongmee)
                .pass()
                .application();
        Applicant kyungho = KYUNGHO_APPLICANT(recruitment).applicant();
        Application kyunghoApplication = KYUNGHO_APPLICATION(kyungho)
                .application();

        // when
        recruitmentService.sendOutcomeEmail(publisher.getId());

        // then
        Application updatedKyunhoApplication = applicationRepository.findById(kyunghoApplication.getId()).get();
        assertAll(
                () -> assertThat(updatedKyunhoApplication.getOutcome()).isEqualTo(Outcome.FAIL),
                () -> assertThat(events.stream(OutcomeDeterminedEvent.class).count()).isSameAs(1L)
        );
    }
}
