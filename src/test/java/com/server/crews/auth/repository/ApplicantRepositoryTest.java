package com.server.crews.auth.repository;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.Recruitment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApplicantRepositoryTest extends RepositoryTest {
    @Autowired
    private ApplicantRepository applicantRepository;

    @Test
    @DisplayName("한 모집 공고 내에서 이메일은 중복될 수 없다.")
    void validateDuplicatedEmail() {
        // given
        Administrator publisher = createDefaultAdmin();
        String email = "email@gmail.com";
        Recruitment recruitment = createDefaultRecruitment(publisher);

        applicantRepository.save(new Applicant(email, TEST_PASSWORD, recruitment));

        // when & then
        assertThatThrownBy(() -> applicantRepository.save(new Applicant(email, TEST_PASSWORD, recruitment)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("이메일과 모집 공고 id로 사용자를 조회한다.")
    void findByEmailAndRecruitment() {
        // given
        Administrator publisher = createDefaultAdmin();
        Recruitment recruitment = createDefaultRecruitment(publisher);
        Applicant applicant = createDefaultApplicant("test@gmail.com", recruitment);

        // when
        Optional<Applicant> foundApplicant = applicantRepository.findByEmailAndRecruitment(applicant.getEmail(), recruitment);

        // then
        assertThat(foundApplicant).isNotEmpty();
    }
}
