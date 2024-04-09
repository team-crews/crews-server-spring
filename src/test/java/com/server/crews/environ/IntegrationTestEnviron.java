package com.server.crews.environ;

import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IntegrationTestEnviron {
    private final ApplicantRepository applicantRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public Recruitment saveRecruitment(final Recruitment recruitment, final List<Section> sections) {
        recruitment.addSections(sections);
        return recruitmentRepository.save(recruitment);
    }

    public Recruitment findById(final Long id) {
        return recruitmentRepository.findById(id).get();
    }
}
