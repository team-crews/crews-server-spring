package com.server.crews.recruitment.presentation;

import static com.server.crews.fixture.QuestionFixture.INTRODUCTION_QUESTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.server.crews.auth.presentation.AuthenticationArgumentResolver;
import com.server.crews.auth.presentation.AuthenticationValidator;
import com.server.crews.environ.presentation.ControllerTest;
import com.server.crews.environ.presentation.TestAuthArgumentResolverConfig;
import com.server.crews.global.config.WebMvcConfiguration;
import com.server.crews.recruitment.application.RecruitmentService;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.domain.QuestionType;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.request.SectionSaveRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@Import(TestAuthArgumentResolverConfig.class)
@WebMvcTest(value = RecruitmentController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                WebMvcConfiguration.class, AuthenticationArgumentResolver.class, AuthenticationValidator.class}))
class RecruitmentControllerTest extends ControllerTest {

    @MockBean
    private RecruitmentService recruitmentService;


    @Test
    @DisplayName("모집 공고를 필수 항목(제목, 마감일)만 포함하여 저장한다.")
    void saveRecruitment() throws Exception {
        // given
        RecruitmentSaveRequest recruitmentSaveRequest = new RecruitmentSaveRequest(null, null, DEFAULT_TITLE, null,
                List.of(), LocalDateTime.now());

        // when & then
        mockMvc.perform(post("/recruitments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recruitmentSaveRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("모집 공고의 제목이 공백인지 검증한다.")
    void validateRecruitmentTitle() throws Exception {
        // given
        String invalidRecruitmentSaveRequest = """
                        {
                            "title": "",
                            "deadline": "2030-09-05T18:00:00.000Z",
                            "sections": []
                        }
                """;

        // when & then
        mockMvc.perform(post("/recruitments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRecruitmentSaveRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("모집공고 제목은 공백일 수 없습니다."));
    }

    @Test
    @DisplayName("모집 공고의 마감일이 null인지 검증한다.")
    void validRecruitmentDeadline() throws Exception {
        // given
        String invalidRecruitmentSaveRequest = """
                        {
                            "title": "모집공고 제목입니다.",
                            "sections": []
                        }
                """;

        // when & then
        mockMvc.perform(post("/recruitments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRecruitmentSaveRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("모집 마감 기한은 null일 수 없습니다."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "2030-09-05 18:00:00"})
    @DisplayName("모집 공고의 마감일 형식을 검증한다.")
    void validateRecruitmentDeadlineForm(String invalidDeadline) throws Exception {
        // given
        String invalidRecruitmentSaveRequest = String.format("""
                        {
                            "title": "모집공고 제목입니다.",
                            "deadline": "%s",
                            "sections": []
                        }
                """, invalidDeadline);

        // when & then
        mockMvc.perform(post("/recruitments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRecruitmentSaveRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("날짜가 ISO8601 형식(yyyy-MM-dd'T'HH:mm:ss.SSS'Z')에 맞지 않습니다."));
    }

    @Test
    @DisplayName("모집 공고의 섹션 이름이 공백인지 검증한다.")
    void validateRecruitmentSectionName() throws Exception {
        // given
        String invalidSectionName = "";
        SectionSaveRequest invalidSectionSaveRequest = new SectionSaveRequest(null, invalidSectionName, null, null);
        RecruitmentSaveRequest invalidRecruitmentSaveRequest = new RecruitmentSaveRequest(null, null, DEFAULT_TITLE,
                null, List.of(invalidSectionSaveRequest), LocalDateTime.now());

        // when & then
        mockMvc.perform(post("/recruitments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRecruitmentSaveRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @ParameterizedTest
    @MethodSource("invalidQuestionSaveRequests")
    @DisplayName("모집 공고 질문의 필수 필드(질문 내용, 순서, 필수 응답 여부, 질문 타입)를 검증한다.")
    void validateRecruitmentQuestionNecessaryField(QuestionSaveRequest invalidQuestionSaveRequest, String errorMessage)
            throws Exception {
        // given
        SectionSaveRequest invalidSectionSaveRequest = new SectionSaveRequest(null, BACKEND_SECTION_NAME, null,
                List.of(invalidQuestionSaveRequest));
        RecruitmentSaveRequest invalidRecruitmentSaveRequest = new RecruitmentSaveRequest(null, null, DEFAULT_TITLE,
                null, List.of(invalidSectionSaveRequest), LocalDateTime.now());

        // when & then
        mockMvc.perform(post("/recruitments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRecruitmentSaveRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    private static Stream<Arguments> invalidQuestionSaveRequests() {
        return Stream.of(
                Arguments.of(new QuestionSaveRequest(null, null, INTRODUCTION_QUESTION, true, 1,
                        300, null, null, List.of()), "질문 타입은 공백일 수 없습니다."),
                Arguments.of(new QuestionSaveRequest(null, QuestionType.NARRATIVE.name(), "", true, 1,
                        300, null, null, List.of()), "질문 내용은 공백일 수 없습니다."),
                Arguments.of(
                        new QuestionSaveRequest(null, QuestionType.NARRATIVE.name(), INTRODUCTION_QUESTION, null, 1,
                                300, null, null, List.of()), "필수 항목 여부는 null일 수 없습니다."),
                Arguments.of(
                        new QuestionSaveRequest(null, QuestionType.NARRATIVE.name(), INTRODUCTION_QUESTION, true, null,
                                300, null, null, List.of()), "질문 순서는 null일 수 없습니다.")
        );
    }

    @Test
    @DisplayName("모집 공고 질문의 질문 타입 필드 형식을 검증한다.")
    void validateRecruitmentQuestionTypeFormat() throws Exception {
        // given
        String invalidRecruitmentSaveRequest = """
                        {
                            "title": "모집 공고 제목",
                            "deadline": "2030-09-12T00:00:00.000Z",
                            "sections": [
                                {
                                    "name": "파트1",
                                    "questions": [
                                        {
                                            "type": "invalid",
                                            "choices": [],
                                            "necessity": true,
                                            "order": 1,
                                            "content": "content"
                                        }
                                    ]
                                }
                            ]
                        }
                """;

        // when & then
        mockMvc.perform(post("/recruitments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRecruitmentSaveRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지 않은 질문 유형(QuestionType) 값입니다. NARRATIVE 혹은 SELECTIVE를 입력해주세요. (대소문자 무관)"));
    }
}
