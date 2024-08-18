package com.server.crews.applicant.presentation;

import com.server.crews.applicant.application.ApplicationService;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.presentation.AdminAuthentication;
import com.server.crews.auth.presentation.ApplicantAuthentication;
import com.server.crews.auth.presentation.AuthenticationRequired;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    /**
     * 지원자가 지원서를 저장한다.
     */
    @PostMapping
    public ResponseEntity<ApplicationDetailsResponse> createApplication(
            @ApplicantAuthentication LoginUser loginUser,
            @RequestBody ApplicationSaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.createApplication(loginUser.userId(), request));
    }

    // Todo: 지원서 수정 api 추가

    /**
     * 특정 지원자의 지원서를 조회한다.
     */
    @GetMapping("/{application-id}")
    public ResponseEntity<ApplicationDetailsResponse> findApplicationDetails(
            @ApplicantAuthentication LoginUser loginUser,
            @PathVariable(value = "application-id") Long applicationId) {
        return ResponseEntity.ok(applicationService.findApplicationDetails(applicationId, loginUser));
    }

    /**
     * 한 공고의 모든 지원서 목록을 조회한다.
     */
    @GetMapping
    public ResponseEntity<List<ApplicationsResponse>> findAllApplicationsByRecruitment(
            @AdminAuthentication LoginUser loginUser) {
        return ResponseEntity.ok(applicationService.findAllApplicationsByRecruitment(loginUser.userId()));
    }

    /**
     * 지원서 평가를 저장한다.
     */
    @PostMapping("/evaluation")
    @AuthenticationRequired
    public ResponseEntity<Void> decideOutcome(@RequestBody EvaluationRequest request) {
        applicationService.decideOutcome(request);
        return ResponseEntity.ok().build();
    }
}
