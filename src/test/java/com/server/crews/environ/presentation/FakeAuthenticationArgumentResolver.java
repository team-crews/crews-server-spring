package com.server.crews.environ.presentation;

import static com.server.crews.fixture.UserFixture.TEST_CLUB_NAME;
import static com.server.crews.fixture.UserFixture.TEST_EMAIL;

import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.controller.AdminAuthentication;
import com.server.crews.auth.controller.AuthenticationArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FakeAuthenticationArgumentResolver extends AuthenticationArgumentResolver {
    public static LoginUser FAKE_ADMIN_LOGIN_USER = new LoginUser(1L, TEST_CLUB_NAME, Role.ADMIN);
    public static LoginUser FAKE_APPLICANT_LOGIN_USER = new LoginUser(1L, TEST_EMAIL, Role.APPLICANT);

    public FakeAuthenticationArgumentResolver() {
        super(null);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return super.supportsParameter(parameter);
    }

    @Override
    public LoginUser resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                     NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (parameter.hasParameterAnnotation(AdminAuthentication.class)) {
            return FAKE_ADMIN_LOGIN_USER;
        }
        return FAKE_APPLICANT_LOGIN_USER;
    }
}
