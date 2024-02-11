package com.server.crews.environ;

import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntegrationTestEnviron {
    private final ApplicantRepository applicantRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final RefreshTokenRepository refreshTokenRepository;
}
