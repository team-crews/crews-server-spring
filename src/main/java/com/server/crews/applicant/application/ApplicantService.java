package com.server.crews.applicant.application;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.response.ApplicantDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicantsResponse;
import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import java.util.List;
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

    public List<ApplicantsResponse> findAllApplicants(final String recruitmentId) {
        List<Applicant> applicants = applicantRepository.findAllByRecruitmentId(recruitmentId);
        return applicants.stream().map(ApplicantsResponse::from).toList();
    }

    public ApplicantDetailsResponse getApplicantDetails(final String applicantId) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
        return ApplicantDetailsResponse.from(applicant);
    }
}
