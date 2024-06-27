package com.server.crews.auth.repository;

import com.server.crews.auth.domain.Member;
import com.server.crews.auth.domain.Role;
import com.server.crews.environ.repository.RepositoryTest;
import com.server.crews.recruitment.domain.Recruitment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static com.server.crews.fixture.MemberFixture.TEST_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("한 모집 공고 내에서 이메일은 중복될 수 없다.")
    void validateDuplicatedEmail() {
        // given
        String email = "email@gmail.com";
        Recruitment recruitment = saveDefaultRecruitment();

        memberRepository.save(new Member(email, TEST_PASSWORD, Role.APPLICANT, recruitment));

        // when & then
        assertThatThrownBy(() -> memberRepository.save(new Member(email, TEST_PASSWORD, Role.APPLICANT, recruitment)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("이메일과 모집 공고 id로 사용자를 조회한다.")
    void findByEmailAndRecruitment() {
        // given
        Recruitment recruitment = saveDefaultRecruitment();
        Member applicant = saveDefaultApplicant(recruitment);

        // when
        Optional<Member> foundMember = memberRepository.findByEmailAndRecruitment(applicant.getEmail(), recruitment);

        // then
        assertThat(foundMember).isNotEmpty();
    }
}
