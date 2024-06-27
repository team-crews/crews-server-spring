package com.server.crews.environ.service;

import com.server.crews.auth.domain.Role;
import com.server.crews.environ.DatabaseCleaner;
import com.server.crews.environ.repository.TestRepository;
import com.server.crews.recruitment.domain.Recruitment;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.server.crews.fixture.MemberFixture.TEST_PASSWORD;
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
        TestRecruitment testRecruitment = new TestRecruitment(serviceTestEnviron);
        testRecruitment.create(DEFAULT_SECRET_CODE, "LIKE LION");
        return testRecruitment;
    }

    protected TestMember JONGMEE_ADMIN(Recruitment recruitment) {
        TestMember testMember = new TestMember(serviceTestEnviron);
        testMember.create("JONGMEE@gmail.com", TEST_PASSWORD, Role.ADMIN, recruitment);
        return testMember;
    }

    protected TestApplicant JONGMEE_APPLICATION(Long recruitmentId) {
        TestApplicant testApplicant = new TestApplicant(serviceTestEnviron);
        testApplicant.create(DEFAULT_SECRET_CODE + " JONGMEE", recruitmentId);
        return testApplicant;
    }

    protected TestApplicant KYUNGHO_APPLICATION(Long recruitmentId) {
        TestApplicant testApplicant = new TestApplicant(serviceTestEnviron);
        testApplicant.create(DEFAULT_SECRET_CODE + " KYUNGHO", recruitmentId);
        return testApplicant;
    }
}
