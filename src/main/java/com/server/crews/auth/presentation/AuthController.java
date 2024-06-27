package com.server.crews.auth.presentation;

import com.server.crews.auth.application.AuthService;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.NewApplicantRequest;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.dto.response.RefreshTokenDto;
import io.swagger.v3.oas.annotations.Operation;
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
    private final AuthService authService;

    @PostMapping("/recruitment/login")
    @Operation(description = "[동아리 관리자] 로그인 해 토큰을 발급 받는다.")
    public ResponseEntity<AccessTokenResponse> createRecruitmentSecretCode(@RequestBody AdminLoginRequest request) {
        AccessTokenResponse accessTokenResponse = authService.loginForAdmin(request);
        RefreshTokenDto refreshTokenDto = authService.createRefreshToken(Role.ADMIN, accessTokenResponse.id());
        ResponseCookie cookie = refreshTokenDto.toCookie();
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(accessTokenResponse);
    }

    @PostMapping("/applicant/login")
    @Operation(description = "[지원자] 로그인 해 토큰을 발급 받는다.")
    public ResponseEntity<AccessTokenResponse> createApplicationSecretCode(@RequestBody NewApplicantRequest request) {
        AccessTokenResponse accessTokenResponse = authService.createApplicationCode(request);
        RefreshTokenDto refreshTokenDto = authService.createRefreshToken(Role.APPLICANT, accessTokenResponse.id());
        ResponseCookie cookie = refreshTokenDto.toCookie();
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(accessTokenResponse);
    }

    @PostMapping("/refresh")
    @Operation(description = "access token을 재발급 받는다.")
    public ResponseEntity<AccessTokenResponse> renew(@CookieValue("refreshToken") final String refreshToken) {
        return ResponseEntity.ok(authService.renew(refreshToken));
    }
}
