package com.server.crews.recruitment.domain;

import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "recruitments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recruitment {
    @Id
    private String id;

    @Indexed(unique = true)
    private String secretCode;

    private String title;

    private String description;

    private Progress progress;

    private List<Section> sections;

    private LocalDateTime deadline;

    public Recruitment(final String secretCode) {
        this.secretCode = secretCode;
        this.progress = Progress.IN_PROGRESS;
    }

    public void updateAll(final RecruitmentSaveRequest request) {
        this.title = request.title();
        this.description = request.description();
        this.sections = request.sections();
        this.deadline = request.deadline();
    }

    public void updateProgress(final Progress progress) {
        this.progress = progress;
    }

    public void updateDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
