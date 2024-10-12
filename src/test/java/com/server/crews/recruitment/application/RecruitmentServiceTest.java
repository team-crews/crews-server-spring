package com.server.crews.recruitment.application;

import static com.server.crews.fixture.QuestionFixture.NARRATIVE_QUESTION;
import static com.server.crews.fixture.QuestionFixture.SELECTIVE_QUESTION;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_QUESTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DEADLINE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static com.server.crews.fixture.RecruitmentFixture.RECRUITMENT_SAVE_REQUEST;
import static com.server.crews.fixture.RecruitmentFixture.SECTION_REQUESTS;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.service.ServiceTest;
import com.server.crews.environ.service.TestRecruitment;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.RecruitmentProgress;
import com.server.crews.recruitment.dto.request.ChoiceSaveRequest;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionSaveRequest;
import com.server.crews.recruitment.dto.response.ChoiceResponse;
import com.server.crews.recruitment.dto.response.QuestionResponse;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    private ApplicationEvents events;

    @Test
    @DisplayName("지원서 양식을 최초로 저장한다.")
    void saveRecruitment() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();

        // when
        RecruitmentDetailsResponse response = recruitmentService.saveRecruitment(publisher.getId(),
                RECRUITMENT_SAVE_REQUEST);

        // then
        assertThat(response.id()).isNotNull();
    }

    @Test
    @DisplayName("모집 마감일은 지금 이전이 될 수 없다.")
    void validateDeadline() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();

        LocalTime time = LocalTime.of(0, 0);
        LocalDate date = LocalDate.now(Clock.system(ZoneId.of("Asia/Seoul"))).minusDays(1);
        LocalDateTime invalidDeadline = LocalDateTime.of(date, time);
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(null, null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, SECTION_REQUESTS, invalidDeadline);

        // when & then
        assertThatThrownBy(() -> recruitmentService.saveRecruitment(publisher.getId(), recruitmentSaveRequest))
                .isInstanceOf(CrewsException.class)
                .hasMessage(CrewsErrorCode.INVALID_DEADLINE.getMessage());
    }

    @Test
    @DisplayName("지원서 양식을 수정 저장한다.")
    void updateRecruitment() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        ChoiceSaveRequest choiceCreateRequest = new ChoiceSaveRequest(null, "선택지 내용");
        QuestionSaveRequest selectiveQuestionCreateRequest = new QuestionSaveRequest(null,
                QuestionType.SELECTIVE.name(),
                STRENGTH_QUESTION, true, 2, null, 1, 2, List.of(choiceCreateRequest));
        SectionSaveRequest sectionsCreateRequest = new SectionSaveRequest(null, FRONTEND_SECTION_NAME,
                DEFAULT_DESCRIPTION,
                List.of(selectiveQuestionCreateRequest));
        RecruitmentSaveRequest recruitmentCreateRequest = new RecruitmentSaveRequest(null, null, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, List.of(sectionsCreateRequest), DEFAULT_DEADLINE);

        RecruitmentDetailsResponse savedRecruitmentResponse = recruitmentService.saveRecruitment(publisher.getId(),
                recruitmentCreateRequest);

        Long recruitmentId = savedRecruitmentResponse.id();
        Long sectionId = savedRecruitmentResponse.sections().get(0).id();
        Long questionId = savedRecruitmentResponse.sections().get(0).questions().get(0).id();
        Long choiceId = savedRecruitmentResponse.sections().get(0).questions().get(0).choices().get(0).id();

        ChoiceSaveRequest choiceSaveRequest = new ChoiceSaveRequest(choiceId, "변경된 선택지 내용");
        QuestionSaveRequest selectiveQuestionSaveRequest = new QuestionSaveRequest(questionId,
                QuestionType.SELECTIVE.name(), "변경된 질문 내용", true, 2, null, 1, 2, List.of(choiceSaveRequest));
        SectionSaveRequest sectionSaveRequest = new SectionSaveRequest(sectionId, "변경된 섹션 이름", DEFAULT_DESCRIPTION,
                List.of(selectiveQuestionSaveRequest));
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(recruitmentId, null, "변경된 모집 공고 제목",
                DEFAULT_DESCRIPTION, List.of(sectionSaveRequest), DEFAULT_DEADLINE);

        // when
        RecruitmentDetailsResponse response = recruitmentService.saveRecruitment(publisher.getId(),
                recruitmentSaveRequest);

        // then
        assertAll(() -> {
            assertThat(response.id()).isEqualTo(savedRecruitmentResponse.id());
            assertThat(response.title()).isEqualTo("변경된 모집 공고 제목");
            assertThat(response.sections()).extracting(SectionResponse::id).containsExactly(sectionId);
            assertThat(response.sections()).extracting(SectionResponse::name).containsExactly("변경된 섹션 이름");
            assertThat(response.sections()).flatExtracting(SectionResponse::questions)
                    .extracting(QuestionResponse::id).containsExactly(questionId);
            assertThat(response.sections()).flatExtracting(SectionResponse::questions)
                    .extracting(QuestionResponse::content).containsExactly("변경된 질문 내용");
            assertThat(response.sections()).flatExtracting(SectionResponse::questions)
                    .flatExtracting(QuestionResponse::choices).extracting(ChoiceResponse::id)
                    .containsExactly(choiceId);
            assertThat(response.sections()).flatExtracting(SectionResponse::questions)
                    .flatExtracting(QuestionResponse::choices).extracting(ChoiceResponse::content)
                    .containsExactly("변경된 선택지 내용");
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
        assertThat(updatedRecruitment.getProgress()).isEqualTo(RecruitmentProgress.IN_PROGRESS);
    }

    @Test
    @DisplayName("작성중인 지원서 양식의 모든 상세정보를 조회한다.")
    void findRecruitmentDetailsInReady() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        LIKE_LION_RECRUITMENT(publisher)
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()));

        // when
        RecruitmentDetailsResponse response = recruitmentService.findRecruitmentDetailsInReady(publisher.getId()).get();

        // then
        List<SectionResponse> sectionResponses = response.sections();
        assertAll(
                () -> assertThat(sectionResponses).hasSize(2),
                () -> assertThat(sectionResponses).flatExtracting(SectionResponse::questions)
                        .filteredOn(questionResponse -> questionResponse.type() == QuestionType.NARRATIVE)
                        .hasSize(2),
                () -> assertThat(sectionResponses).flatExtracting(SectionResponse::questions)
                        .filteredOn(questionResponse -> questionResponse.type() == QuestionType.SELECTIVE)
                        .hasSize(2),
                () -> assertThat(sectionResponses).flatExtracting(SectionResponse::questions)
                        .filteredOn(questionResponse -> questionResponse.type() == QuestionType.SELECTIVE)
                        .flatExtracting(QuestionResponse::choices)
                        .hasSize(6)
        );
    }

    @Test
    @DisplayName("수정된 모집 마감 기한은 기존 기한 이후이다.")
    void validateModifiedDeadline() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher).start().recruitment();

        LocalDateTime invalidDeadline = recruitment.getDeadline().minusDays(1);
        DeadlineUpdateRequest request = new DeadlineUpdateRequest(invalidDeadline);

        // when & then
        assertThatThrownBy(() -> recruitmentService.updateDeadline(publisher.getId(), request))
                .isInstanceOf(CrewsException.class)
                .hasMessage(CrewsErrorCode.INVALID_MODIFIED_DEADLINE.getMessage());
    }

    @Test
    @DisplayName("모집 기한 수정은 모집 진행 중에만 할 수 있다.")
    void validateRecruitmentProgressWhenUpdateDeadline() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher).recruitment();

        LocalDateTime invalidDeadline = recruitment.getDeadline().plusDays(1);
        DeadlineUpdateRequest request = new DeadlineUpdateRequest(invalidDeadline);

        // when & then
        assertThatThrownBy(() -> recruitmentService.updateDeadline(publisher.getId(), request))
                .isInstanceOf(CrewsException.class)
                .hasMessage(CrewsErrorCode.INVALID_MODIFIED_DEADLINE.getMessage());
    }

    @Test
    @DisplayName("모집 공고 상태를 변경하고 지원 결과 이메일을 전송한다.")
    void announceRecruitmentOutcome() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher)
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment();
        Applicant jongmee = JONGMEE_APPLICANT().applicant();
        JONGMEE_APPLICATION(jongmee, recruitment).pass();
        Applicant kyungho = KYUNGHO_APPLICANT().applicant();
        KYUNGHO_APPLICATION(kyungho, recruitment);

        // when
        recruitmentService.announceRecruitmentOutcome(publisher.getId());

        // then
        Recruitment updatedRecruitment = recruitmentRepository.findById(recruitment.getId()).get();
        assertAll(
                () -> assertThat(updatedRecruitment.getProgress()).isEqualTo(RecruitmentProgress.ANNOUNCED),
                () -> assertThat(events.stream(OutcomeDeterminedEvent.class).count()).isSameAs(1L)
        );
    }

    @Test
    @DisplayName("모집 공고 결과 발표가 이미 완료됐을 때 지원 결과 이메일을 전송할 수 없다.")
    void validateAlreadyAnnouncedRecruitment() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        TestRecruitment testRecruitment = LIKE_LION_RECRUITMENT(publisher)
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()));
        Applicant jongmee = JONGMEE_APPLICANT().applicant();
        JONGMEE_APPLICATION(jongmee, testRecruitment.recruitment()).pass();
        Applicant kyungho = KYUNGHO_APPLICANT().applicant();
        KYUNGHO_APPLICATION(kyungho, testRecruitment.recruitment());
        testRecruitment.announce();

        // when & then
        assertThatThrownBy(() -> recruitmentService.announceRecruitmentOutcome(publisher.getId()))
                .isInstanceOf(CrewsException.class)
                .hasMessage(CrewsErrorCode.ALREADY_ANNOUNCED.getMessage());
    }
}
