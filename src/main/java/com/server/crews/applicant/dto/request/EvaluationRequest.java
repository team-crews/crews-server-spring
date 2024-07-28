package com.server.crews.applicant.dto.request;

import java.util.List;

public record EvaluationRequest(Long recruitmentId, List<Long> passApplicationIds) {
}
