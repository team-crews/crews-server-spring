package com.server.crews.recruitment.dto.request;

import com.server.crews.recruitment.presentation.DateTimeFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ClosingDateUpdateRequest(
        @NotNull(message = "모집 마감일은 null일 수 없습니다.")
        @DateTimeFormat
        LocalDateTime closingDate
) {
}
