package com.server.crews.applicant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ApplicationSectionSaveRequest(
        @NotNull(message = "섹션 id는 null일 수 없습니다.")
        Long sectionId,
        @Valid
        @NotNull(message = "답변 리스트는 null일 수 없습니다.")
        List<AnswerSaveRequest> answers
) {
}
