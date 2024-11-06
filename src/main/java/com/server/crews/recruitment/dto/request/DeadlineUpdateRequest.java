package com.server.crews.recruitment.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DeadlineUpdateRequest(
        @NotNull(message = "모집 마감일은 null일 수 없습니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        LocalDateTime deadline
) {
}
