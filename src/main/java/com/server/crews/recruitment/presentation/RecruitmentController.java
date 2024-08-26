package com.server.crews.recruitment.presentation;

import com.server.crews.auth.dto.LoginUser;
import com.server.crews.auth.presentation.AdminAuthentication;
import com.server.crews.recruitment.application.RecruitmentService;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.RecruitmentStateInProgressResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {
    private final RecruitmentService recruitmentService;

    /**
     * 지원서 양식을 저장한다.
     */
    @PostMapping
    public ResponseEntity<RecruitmentDetailsResponse> saveRecruitment(
            @AdminAuthentication LoginUser loginUser, @RequestBody @Valid RecruitmentSaveRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recruitmentService.saveRecruitment(loginUser.userId(), request));
    }

    /**
     * 모집을 시작한다.
     */
    @PatchMapping("/in-progress")
    public ResponseEntity<Void> startRecruiting(@AdminAuthentication LoginUser loginUser) {
        recruitmentService.startRecruiting(loginUser.userId());
        return ResponseEntity.ok().build();
    }

    /**
     * 모집 중 지원 상태를 조회한다.
     */
    @GetMapping("/in-progress")
    public ResponseEntity<RecruitmentStateInProgressResponse> getRecruitmentStateInProgress(
            @AdminAuthentication LoginUser loginUser) {
        return ResponseEntity.ok(recruitmentService.findRecruitmentStateInProgress(loginUser.userId()));
    }

    /**
     * 작성중인 모집 공고 상세 정보를 조회한다.
     */
    @GetMapping("/ready")
    public ResponseEntity<RecruitmentDetailsResponse> getRecruitmentDetailsInReady(@AdminAuthentication LoginUser loginUser) {
        return ResponseEntity.ok(recruitmentService.findRecruitmentDetailsInReady(loginUser.userId()));
    }

    /**
     * 모집 공고 상세 정보를 모집 공고 코드로 조회한다.
     */
    @GetMapping
    public ResponseEntity<RecruitmentDetailsResponse> getRecruitmentDetailsByCode(
            @RequestParam(value = "code") String code) {
        return ResponseEntity.ok(recruitmentService.findRecruitmentDetailsByCode(code));
    }

    /**
     * 모집 마감기한을 변경한다.
     */
    @PatchMapping("/deadline")
    public ResponseEntity<Void> updateDeadline(
            @AdminAuthentication LoginUser loginUser,
            @RequestBody DeadlineUpdateRequest request) {
        recruitmentService.updateDeadline(loginUser.userId(), request);
        return ResponseEntity.ok().build();
    }

    /**
     * 모든 지원자에게 지원 결과 메일을 전송한다.
     */
    @PostMapping("/announcement")
    public ResponseEntity<Void> sendOutcomeEmail(@AdminAuthentication LoginUser loginUser) {
        recruitmentService.announceRecruitmentOutcome(loginUser.userId());
        return ResponseEntity.ok().build();
    }
}
