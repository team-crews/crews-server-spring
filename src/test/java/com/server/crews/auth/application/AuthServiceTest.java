package com.server.crews.auth.application;

import com.server.crews.auth.domain.Member;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginMember;
import com.server.crews.environ.service.ServiceTest;
import com.server.crews.recruitment.domain.Recruitment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceTest extends ServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("액세스 토큰으로 인증된 사용자를 조회한다.")
    void findAuthentication() {
        // given
        Recruitment recruitment = LIKE_LION_RECRUITMENT().recruitment();
        Member member = JONGMEE_APPLICANT(recruitment).member();
        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, member.getEmail());

        // when
        LoginMember loginMember = authService.findAuthentication(accessToken);

        // then
        assertThat(loginMember.email()).isEqualTo(member.getEmail());
    }
}
