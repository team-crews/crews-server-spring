package com.server.crews.applicant.application;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.request.AnswerSaveRequest;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.response.ApplicantAnswersResponse;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.applicant.repository.NarrativeAnswerRepository;
import com.server.crews.applicant.repository.SelectiveAnswerRepository;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.service.ServiceTest;
import com.server.crews.environ.service.TestRecruitment;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.SelectiveQuestion;
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

import static com.server.crews.fixture.ApplicationFixture.DEFAULT_MAJOR;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NAME;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_NARRATIVE_ANSWER;
import static com.server.crews.fixture.ApplicationFixture.DEFAULT_STUDENT_NUMBER;
import static com.server.crews.fixture.QuestionFixture.NARRATIVE_QUESTION;
import static com.server.crews.fixture.QuestionFixture.SELECTIVE_QUESTION;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static com.server.crews.fixture.SectionFixture.FRONTEND_SECTION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@RecordApplicationEvents
class ApplicationServiceTest extends ServiceTest {
    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private NarrativeAnswerRepository narrativeAnswerRepository;

    @Autowired
    private SelectiveAnswerRepository selectiveAnswerRepository;

    @Autowired
    ApplicationEvents events;

    @ParameterizedTest
    @MethodSource("provideAnswersAndCount")
    @DisplayName("답변을 작성한 지원서를 저장한다.")
    void createApplication(List<AnswerSaveRequest> answerSaveRequests, int expectedSavedNarrativeAnsCount, int expectedSavedSelectiveAnsCount) {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher)
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment();
        Applicant applicant = JONGMEE_APPLICANT(recruitment).applicant();
        Application application = JONGMEE_APPLICATION(applicant).application();

        ApplicationSaveRequest saveRequest = new ApplicationSaveRequest(
                DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, DEFAULT_NAME, answerSaveRequests);

        // when
        applicationService.createApplication(applicant.getId(), saveRequest);

        // then
        List<NarrativeAnswer> savedNarrativeAnswers = narrativeAnswerRepository.findAllByApplication(application);
        List<SelectiveAnswer> savedSelectiveAnswers = selectiveAnswerRepository.findAllByApplication(application);
        assertAll(
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
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher)
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment();
        Applicant applicant = JONGMEE_APPLICANT(recruitment).applicant();
        Application application = JONGMEE_APPLICATION(applicant).application();

        List<AnswerSaveRequest> invalidAnswerSaveRequests = List.of(
                new AnswerSaveRequest(QuestionType.NARRATIVE, 3L, DEFAULT_NARRATIVE_ANSWER, null));
        ApplicationSaveRequest saveRequest = new ApplicationSaveRequest(
                DEFAULT_STUDENT_NUMBER, DEFAULT_MAJOR, DEFAULT_NAME, invalidAnswerSaveRequests);

        // when & then
        assertThatThrownBy(() -> applicationService.createApplication(applicant.getId(), saveRequest))
                .isInstanceOf(CrewsException.class);
    }

    @Test
    @DisplayName("지원자의 모든 서술형, 선택형 문항 답변을 조회한다.")
    void findAllApplicantAnswers() {
        // given
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        TestRecruitment testRecruitment = LIKE_LION_RECRUITMENT(publisher)
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()));
        Applicant applicant = JONGMEE_APPLICANT(testRecruitment.recruitment()).applicant();
        NarrativeQuestion narrativeQuestion = testRecruitment.narrativeQuestions().get(0);
        SelectiveQuestion selectiveQuestion = testRecruitment.selectiveQuestions().get(0);
        List<Choice> choices = testRecruitment.choices(0);
        Application application = JONGMEE_APPLICATION(applicant)
                .addNarrativeAnswers(narrativeQuestion, "안녕하세요")
                .saveSelectiveAnswers(selectiveQuestion, choices.get(0))
                .saveSelectiveAnswers(selectiveQuestion, choices.get(1))
                .application();

        // when
        ApplicantAnswersResponse response = applicationService.findAllApplicantAnswers(application.getId());

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
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher)
                .addSection(BACKEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .addSection(FRONTEND_SECTION_NAME, List.of(NARRATIVE_QUESTION()), List.of(SELECTIVE_QUESTION()))
                .recruitment();
        Applicant jongmee = JONGMEE_APPLICANT(recruitment).applicant();
        Application jongmeeApplication = JONGMEE_APPLICATION(jongmee)
                .decideOutcome(Outcome.PASS)
                .application();
        Applicant kyungho = KYUNGHO_APPLICANT(recruitment).applicant();
        Application kyunghoApplication = KYUNGHO_APPLICATION(kyungho)
                .application();

        // when
        applicationService.sendOutcomeEmail(recruitment);

        // then
        Application updatedKyunhoApplication = applicationRepository.findById(kyunghoApplication.getId()).get();
        assertAll(
                () -> assertThat(updatedKyunhoApplication.getOutcome()).isEqualTo(Outcome.FAIL),
                () -> assertThat(events.stream(OutcomeDeterminedEvent.class).count()).isSameAs(1L)
        );
    }
}
