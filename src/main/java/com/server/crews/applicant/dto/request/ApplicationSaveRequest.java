package com.server.crews.applicant.dto.request;

import java.util.List;

public record ApplicationSaveRequest(
        String studentNumber, String major, String email, String name, List<AnswerSaveRequest> answers) {
}
