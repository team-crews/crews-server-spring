package com.server.crews.recruitment.controller;

import com.server.crews.auth.controller.AdminAuthentication;
import com.server.crews.auth.dto.LoginUser;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.RecruitmentProgressResponse;
import com.server.crews.recruitment.dto.response.RecruitmentSearchResponse;
import com.server.crews.recruitment.dto.response.RecruitmentStateInProgressResponse;
import com.server.crews.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
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
     * 모집 공고 상세 정보를 저장한다.
     */
    @PostMapping
    public ResponseEntity<RecruitmentDetailsResponse> saveRecruitment(
            @AdminAuthentication LoginUser loginUser, @RequestBody @Valid RecruitmentSaveRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recruitmentService.saveRecruitment(loginUser.userId(), request));
    }

    /**
     * 모집 공고 제목 목록을 prefix로 검색한다.
     */
    @GetMapping("/search")
    public ResponseEntity<List<RecruitmentSearchResponse>> searchRecruitmentsTitle(
            @RequestParam(value = "prefix") String prefix,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recruitmentService.searchRecruitmentsTitle(prefix, limit));
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
    public ResponseEntity<RecruitmentDetailsResponse> getRecruitmentDetailsInReady(
            @AdminAuthentication LoginUser loginUser) {
        Optional<RecruitmentDetailsResponse> recruitmentDetailsResponse = recruitmentService.findRecruitmentDetailsInReady(
                loginUser.userId());
        return recruitmentDetailsResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
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
     * 모집 공고 상세 정보를 모집 공고 제목으로 조회한다.
     */
    @GetMapping("/search-by")
    public ResponseEntity<RecruitmentDetailsResponse> getRecruitmentDetailsByTitle(
            @RequestParam(value = "title") String title) {
        return ResponseEntity.ok(recruitmentService.findRecruitmentDetailsByTitle(title));
    }

    /**
     * 모집 공고의 단계를 조회한다.
     */
    @GetMapping("/progress")
    public ResponseEntity<RecruitmentProgressResponse> getRecruitmentProgress(
            @AdminAuthentication LoginUser loginUser) {
        return ResponseEntity.ok(recruitmentService.findRecruitmentProgress(loginUser.userId()));
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
