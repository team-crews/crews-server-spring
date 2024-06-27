package com.server.crews.auth.application;

import com.server.crews.auth.domain.Member;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
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

class MemberServiceTest extends ServiceTest {
    @Autowired
    private MemberService memberService;

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
        AccessTokenResponse accessTokenResponse = memberService.loginForAdmin(request);

        // then
        checkLoginSuccess(accessTokenResponse);
    }

    @Test
    @DisplayName("[동아리 관리자] 가입된 관리자가 로그인 요청을 하면 액세스 토큰을 발급한다.")
    void loginAdmin() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT().recruitment();
        Member member = MEORU_ADMIN(recruitment).member();
        AdminLoginRequest request = new AdminLoginRequest(member.getEmail(), member.getPassword());

        // when
        AccessTokenResponse accessTokenResponse = memberService.loginForAdmin(request);

        // then
        checkLoginSuccess(accessTokenResponse);
    }

    @Test
    @DisplayName("[동아리 지원자] 가입되지 않은 지원자가 로그인 요청을 하면 계정을 생성하고 액세스 토큰을 발급한다.")
    void loginNotSignedUpApplicant() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT().recruitment();
        String email = "new@gamil.com";
        String password = "new password";
        ApplicantLoginRequest request = new ApplicantLoginRequest(recruitment.getSecretCode(), email, password);

        // when
        AccessTokenResponse accessTokenResponse = memberService.loginForApplicant(request);

        // then
        checkLoginSuccess(accessTokenResponse);
    }

    @Test
    @DisplayName("[동아리 지원자] 가입된 지원자가 로그인 요청을 하면 액세스 토큰을 발급한다.")
    void loginApplicant() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT().recruitment();
        Member member = JONGMEE_APPLICANT(recruitment).member();
        ApplicantLoginRequest request = new ApplicantLoginRequest(recruitment.getSecretCode(), member.getEmail(), member.getPassword());

        // when
        AccessTokenResponse accessTokenResponse = memberService.loginForApplicant(request);

        // then
        checkLoginSuccess(accessTokenResponse);
    }

    void checkLoginSuccess(AccessTokenResponse accessTokenResponse) {
        List<Member> createdMembers = memberRepository.findAll();
        List<Recruitment> createdRecruitments = recruitmentRepository.findAll();
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(createdMembers).hasSize(1);
            softAssertions.assertThat(createdRecruitments).hasSize(1);
            softAssertions.assertThat(accessTokenResponse.accessToken()).isNotNull();
        });
    }
}
