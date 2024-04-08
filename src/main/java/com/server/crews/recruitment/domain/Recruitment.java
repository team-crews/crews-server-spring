package com.server.crews.recruitment.domain;

import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(unique = true)
    private String secretCode;

    private String title;

    private String clubName;

    private String description;

    private Progress progress;

    private List<Section> sections;

    private LocalDateTime deadline;

    public Recruitment(final String secretCode) {
        this.secretCode = secretCode;
        this.progress = Progress.IN_PROGRESS;
    }

    public void updateAll(final RecruitmentSaveRequest request) {
        setQuestionsOrder(request.sections());

        this.title = request.title();
        this.clubName = request.clubName();
        this.description = request.description();
        this.sections = request.sections();
        this.deadline = request.deadline();
    }

    public void setQuestionsOrder(List<Section> sectionsInRequest) {
        int sequence = 1;
        for (Section section : sectionsInRequest) {
            sequence = section.setQuestionOrder(sequence);
        }
    }

    public void updateProgress(final Progress progress) {
        this.progress = progress;
    }

    public void updateDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
