package com.server.crews.auth.application;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginUser;
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
        Administrator publisher = LIKE_LION_ADMIN().administrator();
        Recruitment recruitment = LIKE_LION_RECRUITMENT(publisher).recruitment();
        Applicant applicant = JONGMEE_APPLICANT(recruitment).applicant();
        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, applicant.getEmail());

        // when
        LoginUser loginUser = authService.findAuthentication(accessToken);

        // then
        assertThat(loginUser.userId()).isEqualTo(applicant.getId());
    }
}
