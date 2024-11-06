package com.server.crews.auth.controller;

import com.server.crews.auth.service.AuthService;
import com.server.crews.auth.dto.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {
    private final AuthService authService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AdminAuthentication.class) ||
                parameter.hasParameterAnnotation(ApplicantAuthentication.class);
    }

    @Override
    public LoginUser resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                     NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = AuthorizationExtractor.extract(request);
        if (parameter.hasParameterAnnotation(AdminAuthentication.class)
                && parameter.hasParameterAnnotation(ApplicantAuthentication.class)) {
            return authService.findAuthentication(accessToken);
        }
        if (parameter.hasParameterAnnotation(AdminAuthentication.class)) {
            return authService.findAdminAuthentication(accessToken);
        }
        return authService.findApplicantAuthentication(accessToken);
    }
}
