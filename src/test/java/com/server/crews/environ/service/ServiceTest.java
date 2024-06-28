package com.server.crews.environ.service;

import com.server.crews.auth.domain.Member;
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

    protected TestRecruitment LIKE_LION_RECRUITMENT() {
        TestRecruitment testRecruitment = new TestRecruitment(serviceTestEnviron);
        testRecruitment.create(DEFAULT_CODE, "LIKE LION");
        return testRecruitment;
    }

    protected TestMember MEORU_ADMIN(Recruitment recruitment) {
        TestMember testMember = new TestMember(serviceTestEnviron);
        testMember.create("MEORU@gmail.com", TEST_PASSWORD, Role.ADMIN, recruitment);
        return testMember;
    }

    protected TestMember JONGMEE_APPLICANT(Recruitment recruitment) {
        TestMember testMember = new TestMember(serviceTestEnviron);
        testMember.create("JONGMEE@gmail.com", TEST_PASSWORD, Role.APPLICANT, recruitment);
        return testMember;
    }

    protected TestMember KYUNGHO_APPLICANT(Recruitment recruitment) {
        TestMember testMember = new TestMember(serviceTestEnviron);
        testMember.create("KYUNGHO@gmail.com", TEST_PASSWORD, Role.APPLICANT, recruitment);
        return testMember;
    }

    protected TestApplication JONGMEE_APPLICATION(Member member) {
        TestApplication testApplication = new TestApplication(serviceTestEnviron);
        testApplication.create(member, "20202020", "생명과학", "종미");
        return testApplication;
    }

    protected TestApplication KYUNGHO_APPLICATION(Member member) {
        TestApplication testApplication = new TestApplication(serviceTestEnviron);
        testApplication.create(member, "20202021", "컴퓨터공학", "경호");
        return testApplication;
    }
}
