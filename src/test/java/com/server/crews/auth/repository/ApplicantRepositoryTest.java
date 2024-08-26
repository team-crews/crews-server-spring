package com.server.crews.auth.repository;

import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.server.crews.auth.domain.Applicant;
import com.server.crews.environ.repository.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

class ApplicantRepositoryTest extends RepositoryTest {
    @Autowired
    private ApplicantRepository applicantRepository;

    @Test
    @DisplayName("지원자 이메일은 중복될 수 없다.")
    void validateDuplicatedEmail() {
        // given
        String email = "email@gmail.com";

        applicantRepository.save(new Applicant(email, TEST_PASSWORD));

        // when & then
        assertThatThrownBy(() -> applicantRepository.save(new Applicant(email, TEST_PASSWORD)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
