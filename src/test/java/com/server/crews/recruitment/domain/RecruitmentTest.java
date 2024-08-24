package com.server.crews.recruitment.domain;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_TITLE;
import static com.server.crews.fixture.UserFixture.TEST_ADMIN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecruitmentTest {

    @Test
    @DisplayName("모집 마감일은 지금 이전이 될 수 없다.")
    void validateDeadline() {
        // given
        LocalTime time = LocalTime.of(0, 0);
        LocalDate date = LocalDate.now(Clock.system(ZoneId.of("Asia/Seoul"))).minusDays(1);
        LocalDateTime invalidDeadline = LocalDateTime.of(date, time);

        // when & then
        assertThatThrownBy(() -> new Recruitment(null, DEFAULT_CODE, DEFAULT_TITLE, DEFAULT_DESCRIPTION,
                invalidDeadline, TEST_ADMIN(), List.of()))
                .isInstanceOf(CrewsException.class)
                .hasMessage(ErrorCode.INVALID_DEADLINE.getMessage());
    }
}
