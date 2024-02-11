package com.server.crews.applicant.presentation;

import com.server.crews.applicant.application.ApplicantService;
import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicantDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicantsResponse;
import com.server.crews.auth.presentation.Authentication;
import com.server.crews.auth.presentation.AuthenticationRequired;
import com.server.crews.recruitment.domain.Recruitment;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/applicants")
@RequiredArgsConstructor
public class ApplicantController {
    private final ApplicantService applicantService;

    @PostMapping
    @Operation(description = "지원자가 지원서를 저장한다.")
    public ResponseEntity<Void> saveApplication(
            @Authentication final Applicant accessedApplicant,
            @RequestBody final ApplicationSaveRequest request) {
        applicantService.saveApplication(accessedApplicant, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @AuthenticationRequired
    @Operation(description = "한 공고의 모든 지원자 목록을 조회한다.")
    public ResponseEntity<List<ApplicantsResponse>> findAllApplicants(
            @RequestParam(value = "recruitment-id") final String recruitmentId) {
        return ResponseEntity.ok(applicantService.findAllApplicants(recruitmentId));
    }

    @GetMapping("/{applicant-id}")
    @AuthenticationRequired
    @Operation(description = "특정 지원자의 지원서를 조회한다.")
    public ResponseEntity<ApplicantDetailsResponse> getApplicantDetails(
            @PathVariable(value = "applicant-id") final String applicantId) {
        return ResponseEntity.ok(applicantService.getApplicantDetails(applicantId));
    }

    @PatchMapping("/{applicant-id}/evaluation")
    @AuthenticationRequired
    @Operation(description = "지원자의 합/불을 결정한다.")
    public ResponseEntity<Void> decideOutcome(
            @RequestBody final EvaluationRequest request,
            @PathVariable(value = "applicant-id") final String applicantId) {
        applicantService.decideOutcome(request, applicantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/announcement")
    @Operation(description = "모든 지원자에게 지원 결과 메일을 전송한다.")
    public ResponseEntity<Void> sendOutcomeEmail(
            @Authentication final Recruitment accessedRecruitment) {
        applicantService.sendOutcomeEmail(accessedRecruitment);
        return ResponseEntity.ok().build();
    }
}
