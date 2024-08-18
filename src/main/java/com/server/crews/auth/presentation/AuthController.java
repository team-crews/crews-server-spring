package com.server.crews.auth.presentation;

import com.server.crews.auth.application.AuthService;
import com.server.crews.auth.application.RefreshTokenService;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.RefreshTokenWithValidity;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthController {
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    /**
     * [동아리 관리자] 로그인 해 토큰을 발급 받는다. 모집 공고가 존재하지 않는다면 모집 공고를 새로 생성한다.
     */
    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> loginForAdmin(@RequestBody AdminLoginRequest request) {
        LoginResponse loginResponse = authService.loginForAdmin(request);
        RefreshTokenWithValidity refreshTokenWithValidity = refreshTokenService.createRefreshToken(Role.ADMIN,
                loginResponse.userId());
        ResponseCookie cookie = refreshTokenWithValidity.toCookie();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    /**
     * [지원자] 로그인 해 토큰을 발급 받는다.
     */
    @PostMapping("/applicant/login")
    public ResponseEntity<LoginResponse> loginForApplicant(@RequestBody ApplicantLoginRequest request) {
        LoginResponse loginResponse = authService.loginForApplicant(request);
        RefreshTokenWithValidity refreshTokenWithValidity = refreshTokenService.createRefreshToken(Role.APPLICANT,
                loginResponse.userId());
        ResponseCookie cookie = refreshTokenWithValidity.toCookie();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    /**
     * access token을 재발급 받는다.
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> renew(@CookieValue("refreshToken") String refreshToken) {
        return ResponseEntity.ok(refreshTokenService.renew(refreshToken));
    }
}
