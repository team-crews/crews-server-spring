package com.server.crews.auth.controller;

import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.auth.service.AuthService;
import com.server.crews.auth.service.RefreshTokenCookieGenerator;
import com.server.crews.auth.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenCookieGenerator refreshTokenCookieGenerator;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService,
                          RefreshTokenCookieGenerator refreshTokenCookieGenerator,
                          @Qualifier("refreshTokenStorageFailureHandler") RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
        this.refreshTokenCookieGenerator = refreshTokenCookieGenerator;
    }

    /**
     * [동아리 관리자] 회원가입하고 토큰을 발급 받는다.
     */
    @PostMapping("/admin/register")
    public ResponseEntity<TokenResponse> registerForAdmin(@RequestBody AdminLoginRequest request) {
        TokenResponse tokenResponse = authService.registerForAdmin(request);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(Role.ADMIN, tokenResponse.username());
        ResponseCookie cookie = refreshTokenCookieGenerator.generateWithDefaultValidity(refreshToken.getToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(tokenResponse);
    }

    /**
     * [동아리 관리자] 로그인 해 토큰을 발급 받는다.
     */
    @PostMapping("/admin/login")
    public ResponseEntity<TokenResponse> loginForAdmin(@RequestBody AdminLoginRequest request) {
        TokenResponse tokenResponse = authService.loginForAdmin(request);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(Role.ADMIN, tokenResponse.username());
        ResponseCookie cookie = refreshTokenCookieGenerator.generateWithDefaultValidity(refreshToken.getToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(tokenResponse);
    }

    /**
     * [지원자] 회원가입하고 토큰을 발급 받는다.
     */
    @PostMapping("/applicant/register")
    public ResponseEntity<TokenResponse> registerForApplicant(@RequestBody ApplicantLoginRequest request) {
        TokenResponse tokenResponse = authService.registerForApplicant(request);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(Role.APPLICANT, tokenResponse.username());
        ResponseCookie cookie = refreshTokenCookieGenerator.generateWithDefaultValidity(refreshToken.getToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(tokenResponse);
    }

    /**
     * [지원자] 로그인 해 토큰을 발급 받는다.
     */
    @PostMapping("/applicant/login")
    public ResponseEntity<TokenResponse> loginForApplicant(@RequestBody ApplicantLoginRequest request) {
        TokenResponse tokenResponse = authService.loginForApplicant(request);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(Role.APPLICANT, tokenResponse.username());
        ResponseCookie cookie = refreshTokenCookieGenerator.generateWithDefaultValidity(refreshToken.getToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(tokenResponse);
    }

    /**
     * access token을 재발급 받는다.
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> renew(@CookieValue("refreshToken") String refreshToken) {
        return ResponseEntity.ok(refreshTokenService.renew(refreshToken)); // FE 와 상의
    }

    /**
     * 로그아웃한다.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AdminAuthentication @ApplicantAuthentication LoginUser loginUser) {
        refreshTokenService.delete(loginUser.username());
        ResponseCookie cookie = refreshTokenCookieGenerator.generate(0, "");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
