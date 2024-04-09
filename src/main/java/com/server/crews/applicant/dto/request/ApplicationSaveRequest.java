package com.server.crews.applicant.dto.request;

import com.server.crews.applicant.domain.Answer;
import java.util.List;

public record ApplicationSaveRequest(
        Long recruitmentId, Long studentNumber, String major, String email, String name, List<Answer> answers) {

}
