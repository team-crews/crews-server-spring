package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Application;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.Recruitment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationRepositoryTest extends RepositoryTest {
    @Autowired
    ApplicationRepository applicationRepository;

    @Test
    @DisplayName("모집 공고의 모든 [지원서 + 지원자 정보]를 조회한다.")
    void findAllWithApplicantByRecruitment() {
        // given
        Administrator admin = createDefaultAdmin();
        Recruitment recruitment = createDefaultRecruitment(admin);
        Applicant applicant = createDefaultApplicant(recruitment);
        Application application = createDefaultApplication(applicant);

        // when
        List<Application> applications = applicationRepository.findAllWithApplicantByRecruitmentId(recruitment.getId());

        // then
        assertThat(applications).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 모집 공고의 모든 [지원서 + 지원자 정보]를 조회한다.")
    void findAllWithApplicantByNotExistingRecruitment() {
        // given
        Long noExistingRecruitmentId = 1L;

        // when
        List<Application> applications = applicationRepository.findAllWithApplicantByRecruitmentId(noExistingRecruitmentId);

        // then
        assertThat(applications).hasSize(0);
    }
}
