package com.server.crews.auth.presentation;

import com.server.crews.auth.application.AuthService;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.request.NewSecretCodeRequest;
import com.server.crews.auth.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/recruitment/secret-code")
    @Operation(description = "지원서 양식에 대한 코드를 생성한다.")
    public ResponseEntity<TokenResponse> createRecruitmentSecretCode(
            @RequestBody final NewSecretCodeRequest request) {
        TokenResponse tokenResponse = authService.createRecruitmentCode(request);
        ResponseCookie cookie = authService.createRefreshToken(Role.ADMIN, tokenResponse.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(tokenResponse);
    }

    @PostMapping("/application/secret-code")
    @Operation(description = "지원서에 대한 코드를 생성한다.")
    public ResponseEntity<TokenResponse> createApplicationSecretCode(
            @RequestBody final NewSecretCodeRequest request) {
        TokenResponse tokenResponse = authService.createApplicationCode(request);
        ResponseCookie cookie = authService.createRefreshToken(Role.APPLICANT, tokenResponse.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(tokenResponse);
    }
}
