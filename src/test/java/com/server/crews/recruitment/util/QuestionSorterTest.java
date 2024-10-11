package com.server.crews.recruitment.util;

import static com.server.crews.fixture.QuestionFixture.BRIGHT_CHOICE;
import static com.server.crews.fixture.QuestionFixture.FAITHFUL_CHOICE;
import static com.server.crews.fixture.QuestionFixture.INTRODUCTION_QUESTION;
import static com.server.crews.fixture.QuestionFixture.METICULOUS_CHOICE;
import static com.server.crews.fixture.QuestionFixture.STRENGTH_QUESTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DEADLINE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static com.server.crews.fixture.SectionFixture.BACKEND_SECTION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.server.crews.recruitment.domain.RecruitmentProgress;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.response.ChoiceResponse;
import com.server.crews.recruitment.dto.response.QuestionResponse;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuestionSorterTest {

    @Test
    @DisplayName("모집 공고 상세 정보를 조회할 때 질문(order 순)과 선택지(id 순)를 정렬한다.")
    void sort() {
        // given
        List<ChoiceResponse> choiceResponses = new ArrayList<>(List.of(
                new ChoiceResponse(3l, FAITHFUL_CHOICE),
                new ChoiceResponse(2l, BRIGHT_CHOICE),
                new ChoiceResponse(1l, METICULOUS_CHOICE)));
        List<QuestionResponse> questionResponses = new ArrayList<>(List.of(
                new QuestionResponse(1l, QuestionType.SELECTIVE, STRENGTH_QUESTION, true, 2, null, 1, 1,
                        choiceResponses),
                new QuestionResponse(2l, QuestionType.NARRATIVE, INTRODUCTION_QUESTION, true, 1, 500, null, null,
                        List.of())));
        List<SectionResponse> sectionResponses = new ArrayList<>(
                List.of(new SectionResponse(1l, BACKEND_SECTION_NAME, DEFAULT_DESCRIPTION, questionResponses)));
        RecruitmentDetailsResponse recruitmentDetailsResponse = new RecruitmentDetailsResponse(1l, DEFAULT_TITLE,
                DEFAULT_DESCRIPTION, RecruitmentProgress.IN_PROGRESS, sectionResponses, DEFAULT_DEADLINE, DEFAULT_CODE);

        // when
        QuestionSorter.sort(recruitmentDetailsResponse);

        // then
        List<QuestionResponse> sortedQuestionResponses = recruitmentDetailsResponse.sections()
                .stream()
                .map(SectionResponse::questions)
                .flatMap(Collection::stream)
                .toList();
        assertAll(() -> {
            assertThat(sortedQuestionResponses).extracting(QuestionResponse::id)
                    .containsExactly(2l, 1l);
            assertThat(sortedQuestionResponses).filteredOn(this::isSelectiveQuestion)
                    .flatExtracting(this::extractChoiceIds)
                    .containsExactly(1L, 2L, 3L);
        });
    }

    private boolean isSelectiveQuestion(QuestionResponse questionResponse) {
        return questionResponse.type() == QuestionType.SELECTIVE;
    }

    private List<Long> extractChoiceIds(QuestionResponse questionResponse) {
        return questionResponse.choices().stream()
                .map(ChoiceResponse::id)
                .toList();
    }
}
