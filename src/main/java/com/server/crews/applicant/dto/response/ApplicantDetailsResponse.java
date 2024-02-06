package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.Answer;
import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.Outcome;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ApplicantDetailsResponse {
    private String id;
    private String recruitmentId;
    private Outcome outcome;
    private Long studentNumber;
    private String major;
    private String email;
    private String name;
    private List<Answer> answers;

    public static ApplicantDetailsResponse from(final Applicant applicant) {
        return ApplicantDetailsResponse.builder()
                .id(applicant.getId())
                .recruitmentId(applicant.getRecruitmentId())
                .outcome(applicant.getOutcome())
                .studentNumber(applicant.getStudentNumber())
                .major(applicant.getMajor())
                .email(applicant.getEmail())
                .name(applicant.getName())
                .answers(applicant.getAnswers())
                .build();
    }
}
