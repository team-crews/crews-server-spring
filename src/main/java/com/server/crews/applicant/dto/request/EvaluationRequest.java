package com.server.crews.applicant.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EvaluationRequest(
        @NotNull(message = "모집 공고 id는 null일 수 없습니다.")
        Long recruitmentId,

        @NotNull(message = "합격 지원서 id 목록은 null일 수 없습니다.")
        List<Long> passApplicationIds
) {
}
