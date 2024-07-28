package com.server.crews.applicant.presentation;

import com.server.crews.applicant.application.ApplicationService;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicationsResponse;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.presentation.ApplicantAuthentication;
import com.server.crews.auth.presentation.AuthenticationRequired;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping
    @Operation(description = "지원자가 지원서를 처음으로 저장한다.")
    public ResponseEntity<ApplicationDetailsResponse> createApplication(
            @ApplicantAuthentication LoginUser loginUser,
            @RequestBody ApplicationSaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.createApplication(loginUser.userId(), request));
    }

    // Todo: 지원서 수정 api 추가

    @GetMapping("/{application-id}")
    @AuthenticationRequired
    @Operation(description = "특정 지원자의 지원서를 조회한다.")
    public ResponseEntity<ApplicationDetailsResponse> findApplicantAnswers(
            @PathVariable(value = "application-id") Long applicationId) {
        return ResponseEntity.ok(applicationService.findAllApplicantAnswers(applicationId));
    }

    @GetMapping
    @AuthenticationRequired
    @Operation(description = "한 공고의 모든 지원자 목록을 조회한다.")
    public ResponseEntity<List<ApplicationsResponse>> findAllApplicants(
            @RequestParam(value = "recruitment-id") Long recruitmentId) {
        return ResponseEntity.ok(applicationService.findAllApplications(recruitmentId));
    }

    @PatchMapping("/{applicant-id}/evaluation")
    @AuthenticationRequired
    @Operation(description = "지원자의 합/불을 결정한다.")
    public ResponseEntity<Void> decideOutcome(
            @RequestBody EvaluationRequest request,
            @PathVariable(value = "applicant-id") Long applicantId) {
        applicationService.decideOutcome(request, applicantId);
        return ResponseEntity.ok().build();
    }
}
