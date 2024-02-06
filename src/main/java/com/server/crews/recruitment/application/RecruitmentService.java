package com.server.crews.recruitment.application;

import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.dto.request.ProgressStateUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;

    public void saveRecruitment(
            final Recruitment accessedRecruitment, final RecruitmentSaveRequest request) {
        Recruitment updatedRecruitment = accessedRecruitment.updateAll(request);
        recruitmentRepository.save(updatedRecruitment);
    }
}
