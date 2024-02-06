package com.server.crews.recruitment.application;

import com.server.crews.auth.domain.Access;
import com.server.crews.recruitment.domain.Recruitment;
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
            final Access access, final RecruitmentSaveRequest request) {
        log.info("id: {}", access.id());
        Recruitment updatedRecruitment = Recruitment.from(request, access.id());
        recruitmentRepository.save(updatedRecruitment);
    }
}
