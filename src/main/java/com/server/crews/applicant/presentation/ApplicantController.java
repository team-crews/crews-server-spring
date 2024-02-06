package com.server.crews.applicant.presentation;

import com.server.crews.applicant.application.ApplicantService;
import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.auth.presentation.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
