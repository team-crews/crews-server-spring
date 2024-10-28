package com.server.crews.applicant.controller;

import com.server.crews.applicant.service.ApplicationService;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.controller.AdminAuthentication;
import com.server.crews.auth.controller.ApplicantAuthentication;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<ApplicationDetailsResponse> saveApplication(
            @ApplicantAuthentication LoginUser loginUser,
            @RequestBody ApplicationSaveRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(applicationService.saveApplication(loginUser.userId(), request));
    }

    /**
     * 동아리 관리자가 특정 지원자의 지원서를 조회한다.
     */
    @GetMapping("/{application-id}")
    public ResponseEntity<ApplicationDetailsResponse> getApplicationDetails(
            @AdminAuthentication LoginUser loginUser,
            @PathVariable(value = "application-id") Long applicationId) {
        return ResponseEntity.ok(applicationService.findApplicationDetails(applicationId, loginUser.userId()));
    }

    /**
     * 지원자가 본인의 지원서를 조회한다.
     */
    @GetMapping("/mine")
    public ResponseEntity<ApplicationDetailsResponse> getMyApplicationDetails(
            @ApplicantAuthentication LoginUser loginUser, @RequestParam("code") String code) {
        Optional<ApplicationDetailsResponse> myApplicationDetails = applicationService.findMyApplicationDetails(
                loginUser.userId(), code);
        return myApplicationDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * 한 공고의 모든 지원서 목록을 조회한다.
     */
    @GetMapping
    public ResponseEntity<List<ApplicationsResponse>> getAllApplicationsByPublisher(
            @AdminAuthentication LoginUser loginUser) {
        return ResponseEntity.ok(applicationService.findAllApplicationsByPublisher(loginUser.userId()));
    }

    /**
     * 지원서 평가를 저장한다.
     */
    @PostMapping("/evaluation")
    public ResponseEntity<Void> evaluate(@AdminAuthentication LoginUser loginUser,
                                         @RequestBody EvaluationRequest request) {
        applicationService.decideOutcome(request, loginUser.userId());
        return ResponseEntity.ok().build();
    }
}
