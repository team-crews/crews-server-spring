package com.server.crews.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.server.crews.fixture.UserFixture.TEST_PASSWORD;
import static com.server.crews.global.exception.ErrorCode.INVALID_EMAIL_PATTERN;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @ParameterizedTest
    @ValueSource(strings = {"jongmee", "jong123@naver", "jong.com"})
    @DisplayName("[실패] 이메일 형식을 검증한다.")
    void validateEmailWithInvalidForm(String invalidEmail) {
        // when & then
        assertThatThrownBy(() -> new Member(invalidEmail, TEST_PASSWORD, Role.APPLICANT))
                .hasMessage(INVALID_EMAIL_PATTERN.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"jongmee@naver.com", "jong123@gmail.com"})
    @DisplayName("[성공] 이메일 형식을 검증한다.")
    void validateEmailWithValidForm(String validEmail) {
        // when & then
        assertThatCode(() -> new Member(validEmail, TEST_PASSWORD, Role.APPLICANT))
                .doesNotThrowAnyException();
    }
}
