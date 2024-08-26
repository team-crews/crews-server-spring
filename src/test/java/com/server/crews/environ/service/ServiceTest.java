package com.server.crews.environ.service;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DEADLINE;
import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.DatabaseCleaner;
import com.server.crews.environ.repository.TestRepository;
import com.server.crews.external.application.EmailService;
import com.server.crews.recruitment.domain.Recruitment;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"schedules.cron.closing-recruitment=0 0 0 31 2 ?"}
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class ServiceTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ServiceTestEnviron serviceTestEnviron;

    @MockBean
    private TestRepository testRepository;

    @MockBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
    }

    protected TestRecruitment LIKE_LION_RECRUITMENT(Administrator publisher) {
        return new TestRecruitment(serviceTestEnviron).create(DEFAULT_CODE, "LIKE LION", DEFAULT_DEADLINE, publisher);
    }

    protected TestAdmin LIKE_LION_ADMIN() {
        return new TestAdmin(serviceTestEnviron).create("LIKE_LION", TEST_PASSWORD);
    }

    protected TestApplicant JONGMEE_APPLICANT() {
        return new TestApplicant(serviceTestEnviron).create("JONGMEE@gmail.com", TEST_PASSWORD);
    }

    protected TestApplicant KYUNGHO_APPLICANT() {
        return new TestApplicant(serviceTestEnviron).create("KYUNGHO@gmail.com", TEST_PASSWORD);
    }

    protected TestApplication JONGMEE_APPLICATION(Applicant applicant, Recruitment recruitment) {
        return new TestApplication(serviceTestEnviron).create(applicant, recruitment, "20202020", "생명과학", "종미");
    }

    protected TestApplication KYUNGHO_APPLICATION(Applicant applicant, Recruitment recruitment) {
        return new TestApplication(serviceTestEnviron).create(applicant, recruitment, "20202021", "컴퓨터공학", "경호");
    }
}
