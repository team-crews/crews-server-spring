package com.server.crews.recruitment.presentation;

import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.presentation.AdminAuthentication;
import com.server.crews.auth.presentation.AuthenticationRequired;
import com.server.crews.recruitment.application.RecruitmentService;
import com.server.crews.recruitment.dto.request.ClosingDateUpdateRequest;
import com.server.crews.recruitment.dto.request.ProgressStateUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {
    private final RecruitmentService recruitmentService;

    @PostMapping
    @Operation(description = "지원서 양식을 저장한다.")
    public ResponseEntity<RecruitmentDetailsResponse> saveRecruitment(
            @AdminAuthentication LoginUser loginUser, @RequestBody RecruitmentSaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recruitmentService.createRecruitment(loginUser.userId(), request));
    }

    @AuthenticationRequired
    @PatchMapping("/{recruitment-id}/progress")
    @Operation(description = "모집 상태를 변경한다.")
    public ResponseEntity<Void> updateProgressState(
            @PathVariable(value = "recruitment-id") Long recruitmentId,
            @RequestBody ProgressStateUpdateRequest request) {
        recruitmentService.updateProgressState(recruitmentId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{recruitment-id}")
    @Operation(description = "지원서 양식 상세 정보를 조회한다.")
    public ResponseEntity<RecruitmentDetailsResponse> getRecruitmentDetails(
            @PathVariable(value = "recruitment-id") Long recruitmentId) {
        return ResponseEntity.ok(recruitmentService.findRecruitmentDetails(recruitmentId));
    }

    @AuthenticationRequired
    @PatchMapping("/{recruitment-id}/closing-date")
    @Operation(description = "지원서 양식의 마감기한을 변경한다.")
    public ResponseEntity<Void> updateProgressState(
            @PathVariable(value = "recruitment-id") Long recruitmentId,
            @RequestBody ClosingDateUpdateRequest request) {
        recruitmentService.updateClosingDate(recruitmentId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/announcement")
    @Operation(description = "모든 지원자에게 지원 결과 메일을 전송한다.")
    public ResponseEntity<Void> sendOutcomeEmail(@AdminAuthentication LoginUser loginUser) {
        recruitmentService.sendOutcomeEmail(loginUser.userId());
        return ResponseEntity.ok().build();
    }
}
