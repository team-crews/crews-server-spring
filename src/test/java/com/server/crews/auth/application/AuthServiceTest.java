package com.server.crews.auth.application;

import com.server.crews.auth.domain.Member;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.repository.MemberRepository;
import com.server.crews.environ.service.ServiceTest;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class AuthServiceTest extends ServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Test
    @DisplayName("[동아리 관리자] 가입되지 않은 관리자가 로그인 요청을 하면 계정과 모집 공고를 생성하고 액세스 토큰을 발급한다.")
    void loginNotSignedUpAdmin() {
        // given
        String email = "new@gamil.com";
        String password = "new password";
        AdminLoginRequest request = new AdminLoginRequest(email, password);

        // when
        AccessTokenResponse accessTokenResponse = authService.loginForAdmin(request);

        // then
        Member createdMember = memberRepository.findById(accessTokenResponse.id()).get();
        List<Recruitment> createdRecruitments = recruitmentRepository.findAll();
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdMember.getEmail()).isEqualTo(email);
            softAssertions.assertThat(accessTokenResponse.accessToken()).isNotNull();
            softAssertions.assertThat(createdRecruitments).hasSize(1);
        });
    }

    @Test
    @DisplayName("[동아리 관리자] 가입된 관리자가 로그인 요청을 하면 액세스 토큰을 발급한다.")
    void loginAdmin() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT().recruitment();
        Member member = JONGMEE_ADMIN(recruitment).member();
        AdminLoginRequest request = new AdminLoginRequest(member.getEmail(), member.getPassword());

        // when
        AccessTokenResponse accessTokenResponse = authService.loginForAdmin(request);

        // then
        List<Member> createdMembers = memberRepository.findAll();
        List<Recruitment> createdRecruitments = recruitmentRepository.findAll();
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdMembers).hasSize(1);
            softAssertions.assertThat(createdRecruitments).hasSize(1);
            softAssertions.assertThat(accessTokenResponse.accessToken()).isNotNull();
        });
    }
}
