package com.server.crews.recruitment.application;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DEADLINE;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.environ.service.ServiceTestEnviron;
import com.server.crews.environ.service.TestAdmin;
import com.server.crews.environ.service.TestRecruitment;
import com.server.crews.recruitment.domain.RecruitmentProgress;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "schedules.cron.closing-recruitment=0/2 * * * * ?"
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClosingRecruitmentTest {
    @Autowired
    private ServiceTestEnviron serviceTestEnviron;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @SpyBean
    private Clock clock;

    @Test
    @DisplayName("일정한 시간 간격으로 모집 마감 기한에 도달한 모집 공고들을 마감한다.")
    void closeRecruitments() throws InterruptedException {
        // given
        BDDMockito.given(clock.instant())
                .willReturn(Instant.parse("2031-09-05T00:00:00Z"));
        Administrator publisher = new TestAdmin(serviceTestEnviron).create("LIKE_LION", TEST_PASSWORD)
                .administrator();
        Recruitment recruitment = new TestRecruitment(serviceTestEnviron)
                .create(DEFAULT_CODE, "LIKE LION", DEFAULT_DEADLINE, publisher).recruitment();

        // when
        Thread.sleep(2000);

        // then
        Recruitment updatedRecruitment = recruitmentRepository.findById(recruitment.getId()).get();
        assertThat(updatedRecruitment.getRecruitmentProgress()).isEqualTo(RecruitmentProgress.COMPLETION);
    }
}
