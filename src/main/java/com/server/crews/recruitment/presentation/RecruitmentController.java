package com.server.crews.recruitment.presentation;

import com.server.crews.auth.domain.Access;
import com.server.crews.auth.presentation.Authentication;
import com.server.crews.recruitment.application.RecruitmentService;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> saveRecruitment(
            @Authentication final Access access,
            @RequestBody final RecruitmentSaveRequest request) {
        recruitmentService.saveRecruitment(access, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
