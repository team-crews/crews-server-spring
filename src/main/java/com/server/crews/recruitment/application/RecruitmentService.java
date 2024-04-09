package com.server.crews.recruitment.application;

import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.ProgressStateUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;

    @Transactional
    public void saveRecruitment(
            final Recruitment accessedRecruitment,
            final RecruitmentSaveRequest request) {
        accessedRecruitment.updateAll(request);
        recruitmentRepository.save(accessedRecruitment);
    }

    @Transactional
    public void updateProgressState(
            final Recruitment accessedRecruitment,
            final ProgressStateUpdateRequest request) {
        accessedRecruitment.updateProgress(request.progress());
        recruitmentRepository.save(accessedRecruitment);
    }

    public RecruitmentDetailsResponse getRecruitmentDetails(final Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        return RecruitmentDetailsResponse.from(recruitment);
    }

    public void updateDeadline(
            final Recruitment accessedRecruitment, final DeadlineUpdateRequest request) {
        accessedRecruitment.updateDeadline(request.deadline());
        recruitmentRepository.save(accessedRecruitment);
    }
}
