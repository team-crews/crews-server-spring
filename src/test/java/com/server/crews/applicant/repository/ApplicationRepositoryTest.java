package com.server.crews.applicant.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.server.crews.applicant.domain.Application;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ApplicationRepositoryTest extends RepositoryTest {
    @Autowired
    ApplicationRepository applicationRepository;

    @Test
    @DisplayName("모집 공고의 모든 지원서를 조회한다.")
    void findAllWithApplicantByRecruitment() {
        // given
        Administrator admin = createDefaultAdmin();
        Recruitment recruitment = createDefaultRecruitment(admin);
        Applicant applicant = createDefaultApplicant("test@gmail.com");
        Application application = createDefaultApplication(applicant.getId(), recruitment);

        // when
        List<Application> applications = applicationRepository.findAllWithRecruitmentByPublisherId(admin.getId());

        // then
        assertThat(applications).hasSize(1);
    }

    @Test
    @DisplayName("모집 공고의 모든 지원서 개수를 조회한다.")
    void countAllByRecruitment() {
        // given
        Administrator admin = createDefaultAdmin();
        Recruitment recruitment = createDefaultRecruitment(admin);
        Applicant firstApplicant = createDefaultApplicant("test1@gmail.com");
        Applicant secondApplicant = createDefaultApplicant("test2@gmail.com");
        createDefaultApplication(firstApplicant.getId(), recruitment);
        createDefaultApplication(secondApplicant.getId(), recruitment);

        // when
        int applicationCount = applicationRepository.countAllByRecruitment(recruitment);

        // then
        assertThat(applicationCount).isEqualTo(2);
    }
}
