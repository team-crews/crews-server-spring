package com.server.crews.auth.presentation;

import com.server.crews.auth.application.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticationValidator {
    private final AuthService authService;

    @Before("@annotation(com.server.crews.auth.presentation.AuthenticationRequired)")
    public void validate() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String accessToken = AuthorizationExtractor.extract(request);
        authService.findApplicantAuthentication(accessToken);
    }
}
