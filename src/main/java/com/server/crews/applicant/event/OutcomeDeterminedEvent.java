package com.server.crews.applicant.event;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.List;

public record OutcomeDeterminedEvent(List<Applicant> applicants, Recruitment recruitment) {

}
