package com.server.crews.applicant.dto.request;

import com.server.crews.applicant.domain.Answer;
import java.util.List;

public record ApplicationSaveRequest(
        String recruitmentId, Long studentNumber, String email, String name, List<Answer> answers) {

}
