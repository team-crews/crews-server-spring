package com.server.crews.applicant.application;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.response.ApplicantAnswersResponse;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.applicant.repository.NarrativeAnswerRepository;
import com.server.crews.applicant.repository.SelectiveAnswerRepository;
import com.server.crews.environ.service.ServiceTest;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.List;
import java.util.stream.Stream;

import static com.server.crews.fixture.ApplicantFixture.*;
import static com.server.crews.fixture.QuestionFixture.NARRATIVE_QUESTION;
import static com.server.crews.fixture.QuestionFixture.SELECTIVE_QUESTION;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@RecordApplicationEvents
class ApplicantServiceTest extends ServiceTest {
    @Autowired
    private ApplicantService applicantService;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private NarrativeAnswerRepository narrativeAnswerRepository;

    @Autowired
    private SelectiveAnswerRepository selectiveAnswerRepository;

    @Autowired
    ApplicationEvents events;

    @ParameterizedTest
    @MethodSource("provideAnswersAndCount")
    @DisplayName("답변을 작성한 지원서를 저장한다.")
    void saveApplication(List<AnswerSaveRequest> answerSaveRequests, int expectedSavedNarrativeAnsCount, int expectedSavedSelectiveAnsCount) {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT()
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment();
        Applicant applicant = JONGMEE_APPLICATION(recruitment.getId())
                .applicant();

        ApplicationSaveRequest saveRequest = new ApplicationSaveRequest(
                DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, DEFAULT_EMAIL, DEFAULT_NAME, answerSaveRequests);

        // when
        applicantService.saveApplication(applicant, saveRequest);

        // then
        Applicant udpatedApplicant = applicantRepository.findById(applicant.getId()).get();
        List<NarrativeAnswer> savedNarrativeAnswers = narrativeAnswerRepository.findAllByApplicantId(applicant.getId());
        List<SelectiveAnswer> savedSelectiveAnswers = selectiveAnswerRepository.findAllByApplicantId(applicant.getId());
        assertAll(
                () -> assertThat(udpatedApplicant.getEmail()).isEqualTo(DEFAULT_EMAIL),
                () -> assertThat(savedNarrativeAnswers).hasSize(expectedSavedNarrativeAnsCount),
                () -> assertThat(savedSelectiveAnswers).hasSize(expectedSavedSelectiveAnsCount)
        );
    }

    private static Stream<Arguments> provideAnswersAndCount() {
        return Stream.of(
                Arguments.of(List.of(
                        new AnswerSaveRequest(QuestionType.NARRATIVE, 2L, DEFAULT_NARRATIVE_ANSWER, null),
                        new AnswerSaveRequest(QuestionType.SELECTIVE, 1L, null, List.of(1L, 2L))
                ), 1, 2),
                Arguments.of(List.of(
                        new AnswerSaveRequest(QuestionType.NARRATIVE, 2L, DEFAULT_NARRATIVE_ANSWER, null)
                ), 1, 0)
        );
    }

    @Test
    @DisplayName("존재하지 않는 질문으로 지원서 작성 저장을 요청하면 예외가 발생한다.")
    void validateQuestionIds() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT()
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment();
        Applicant applicant = JONGMEE_APPLICATION(recruitment.getId())
                .applicant();

        List<AnswerSaveRequest> invalidAnswerSaveRequests = List.of(
                new AnswerSaveRequest(QuestionType.NARRATIVE, 3L, DEFAULT_NARRATIVE_ANSWER, null));
        ApplicationSaveRequest saveRequest = new ApplicationSaveRequest(
                DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, DEFAULT_EMAIL, DEFAULT_NAME, invalidAnswerSaveRequests);

        // when & then
        assertThatThrownBy(() -> applicantService.saveApplication(applicant, saveRequest))
                .isInstanceOf(CrewsException.class);
    }

    @Test
    @DisplayName("지원자의 모든 서술형, 선택형 문항 답변을 조회한다.")
    void findAllApplicantAnswers() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT()
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment();
        Applicant applicant = JONGMEE_APPLICATION(recruitment.getId())
                .addNarrativeAnswers(1L, "안녕하세요")
                .saveSelectiveAnswers(1L, 1L)
                .saveSelectiveAnswers(1L, 2L)
                .applicant();

        // when
        ApplicantAnswersResponse response = applicantService.findAllApplicantAnswers(applicant.getId());

        // then
        assertAll(
                () -> assertThat(response.answerByNarrativeQuestionId()).containsEntry(1L, "안녕하세요"),
                () -> assertThat(response.choiceIdsBySelectiveQuestionId()).containsEntry(1L, List.of(1L, 2L))
        );
    }

    @Test
    @DisplayName("지원 결과를 저장하고 지원 결과 이메일을 전송한다.")
    void sendOutcomeEmail() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT()
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment();
        Applicant jongmeeApplicant = JONGMEE_APPLICATION(recruitment.getId())
                .updateInformation("jp3869@naver.com", "Jongmee")
                .decideOutcome(Outcome.PASS)
                .applicant();
        Applicant kyunghoApplicant = KYUNGHO_APPLICATION(recruitment.getId())
                .updateInformation("jp3869@naver.com", "Kyungho")
                .applicant();

        // when
        applicantService.sendOutcomeEmail(recruitment);

        // then
        Applicant updatedKyunhoApplicant = applicantRepository.findById(kyunghoApplicant.getId()).get();
        assertAll(
                () -> assertThat(updatedKyunhoApplicant.getOutcome()).isEqualTo(Outcome.FAIL),
                () -> assertThat(events.stream(OutcomeDeterminedEvent.class).count()).isSameAs(1L)
        );
    }
}
