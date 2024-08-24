package com.server.crews.recruitment.domain;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static com.server.crews.fixture.UserFixture.TEST_ADMIN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecruitmentTest {

    @Test
    @DisplayName("모집 마감일은 지금 이전이 될 수 없다.")
    void validateDeadline() {
        // given
        LocalDateTime invalidDeadline = LocalDateTime.now().minusDays(1L);

        // when & then
        assertThatThrownBy(() -> new Recruitment(null, DEFAULT_CODE, DEFAULT_TITLE, DEFAULT_DESCRIPTION, invalidDeadline,
                TEST_ADMIN(), List.of()))
                .isInstanceOf(CrewsException.class)
                .hasMessage(ErrorCode.INVALID_DEADLINE.getMessage());
    }
}
