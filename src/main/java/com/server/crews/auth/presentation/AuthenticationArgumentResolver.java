package com.server.crews.auth.presentation;

import com.server.crews.auth.application.AuthService;
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
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authentication.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter, final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = AuthorizationExtractor.extract(request);
        return authService.createAuthentication(accessToken);
    }
}
