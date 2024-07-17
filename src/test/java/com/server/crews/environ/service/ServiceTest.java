package com.server.crews.environ.service;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.DatabaseCleaner;
import com.server.crews.environ.repository.TestRepository;
import com.server.crews.recruitment.domain.Recruitment;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class ServiceTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ServiceTestEnviron serviceTestEnviron;

    @MockBean
    private TestRepository testRepository;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
    }

    protected TestRecruitment LIKE_LION_RECRUITMENT(Administrator publisher) {
        TestRecruitment testRecruitment = new TestRecruitment(serviceTestEnviron);
        testRecruitment.create(DEFAULT_CODE, "LIKE LION", publisher);
        return testRecruitment;
    }

    protected TestAdmin LIKE_LION_ADMIN() {
        TestAdmin testAdmin = new TestAdmin(serviceTestEnviron);
        testAdmin.create("LIKE_LION", TEST_PASSWORD);
        return testAdmin;
    }

    protected TestApplicant JONGMEE_APPLICANT(Recruitment recruitment) {
        TestApplicant testApplicant = new TestApplicant(serviceTestEnviron);
        testApplicant.create("JONGMEE@gmail.com", TEST_PASSWORD, recruitment);
        return testApplicant;
    }

    protected TestApplicant KYUNGHO_APPLICANT(Recruitment recruitment) {
        TestApplicant testApplicant = new TestApplicant(serviceTestEnviron);
        testApplicant.create("KYUNGHO@gmail.com", TEST_PASSWORD, recruitment);
        return testApplicant;
    }

    protected TestApplication JONGMEE_APPLICATION(Applicant applicant) {
        TestApplication testApplication = new TestApplication(serviceTestEnviron);
        testApplication.create(applicant, "20202020", "생명과학", "종미");
        return testApplication;
    }

    protected TestApplication KYUNGHO_APPLICATION(Applicant applicant) {
        TestApplication testApplication = new TestApplication(serviceTestEnviron);
        testApplication.create(applicant, "20202021", "컴퓨터공학", "경호");
        return testApplication;
    }
}
