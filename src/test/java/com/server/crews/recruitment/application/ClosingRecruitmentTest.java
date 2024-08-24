package com.server.crews.recruitment.application;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DEADLINE;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.environ.service.ServiceTestEnviron;
import com.server.crews.environ.service.TestAdmin;
import com.server.crews.environ.service.TestRecruitment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @SpyBean
    private RecruitmentService recruitmentService;

    @Autowired
    private ServiceTestEnviron serviceTestEnviron;

    @Test
    @DisplayName("일정한 시간 간격으로 모집 마감 기한에 도달한 모집 공고들을 마감한다.")
    void closeRecruitments() throws InterruptedException {
        // given
        Administrator publisher = new TestAdmin(serviceTestEnviron).create("LIKE_LION", TEST_PASSWORD)
                .administrator();
        new TestRecruitment(serviceTestEnviron).create(DEFAULT_CODE, "LIKE LION", DEFAULT_DEADLINE, publisher);

        // when
        Thread.sleep(2000);

        // then
        verify(recruitmentService, times(1)).closeRecruitments();
    }
}
