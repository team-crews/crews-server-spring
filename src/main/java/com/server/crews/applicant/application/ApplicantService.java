package com.server.crews.applicant.application;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicantService {
    private final ApplicantRepository applicantRepository;

    public void saveApplication(
            final Applicant accessedApplicant, final ApplicationSaveRequest request) {
        accessedApplicant.updateAll(request);
        applicantRepository.save(accessedApplicant);
    }
}
