package com.server.crews.environ.service;

import com.server.crews.environ.repository.TestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_SECRET_CODE;

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

    protected TestRecruitment LIKE_LION_RECRUITMENT() {
        var testRecruitment = new TestRecruitment(serviceTestEnviron);
        testRecruitment.create(DEFAULT_SECRET_CODE, "LIKE LION");
        return testRecruitment;
    }

    protected TestApplicant JONGMEE(final Long recruitmentId) {
        var testApplicant = new TestApplicant(serviceTestEnviron);
        testApplicant.create(DEFAULT_SECRET_CODE + " JONGMEE", recruitmentId);
        return testApplicant;
    }

    protected TestApplicant KYUNGHO(final Long recruitmentId) {
        var testApplicant = new TestApplicant(serviceTestEnviron);
        testApplicant.create(DEFAULT_SECRET_CODE + " KYUNGHO", recruitmentId);
        return testApplicant;
    }
}
