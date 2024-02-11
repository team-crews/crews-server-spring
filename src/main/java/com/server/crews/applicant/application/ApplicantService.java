package com.server.crews.applicant.application;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.applicant.dto.request.EvaluationRequest;
import com.server.crews.applicant.dto.response.ApplicantDetailsResponse;
import com.server.crews.applicant.dto.response.ApplicantsResponse;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicantService {
    private final ApplicantRepository applicantRepository;
    private final ApplicationEventPublisher eventPublisher;

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

    public void decideOutcome(final EvaluationRequest request, final String applicantId) {
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CrewsException(ErrorCode.APPLICATION_NOT_FOUND));
        applicant.decideOutcome(request.outcome());
        applicantRepository.save(applicant);
    }

    public void sendOutcomeEmail(final Recruitment accessedRecruitment) {
        String recruitmentId = accessedRecruitment.getId();
        List<Applicant> applicants = applicantRepository.findAllByRecruitmentId(recruitmentId);

        applicants.stream().filter(Applicant::isNotDetermined)
                .forEach(applicant -> applicant.decideOutcome(Outcome.FAIL));

        eventPublisher.publishEvent(new OutcomeDeterminedEvent(applicants, accessedRecruitment));
    }
}
