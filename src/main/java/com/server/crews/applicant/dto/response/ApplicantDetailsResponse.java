package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.Outcome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ApplicantDetailsResponse {
    private Long id;
    private Long recruitmentId;
    private Outcome outcome;
    private String studentNumber;
    private String major;
    private String email;
    private String name;

    public static ApplicantDetailsResponse from(final Applicant applicant) {
        return ApplicantDetailsResponse.builder()
                .id(applicant.getId())
                .recruitmentId(applicant.getRecruitmentId())
                .outcome(applicant.getOutcome())
                .studentNumber(applicant.getStudentNumber())
                .major(applicant.getMajor())
                .email(applicant.getEmail())
                .name(applicant.getName())
                .build();
    }
}
