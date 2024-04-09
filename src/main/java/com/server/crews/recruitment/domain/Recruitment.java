package com.server.crews.recruitment.domain;

import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @Column(unique = true)
    private String secretCode;

    private String title;

    private String clubName;

    private String description;

    private Progress progress;

    private LocalDateTime deadline;

    public Recruitment(final String secretCode) {
        this.secretCode = secretCode;
        this.progress = Progress.IN_PROGRESS;
    }

    public void updateAll(final RecruitmentSaveRequest request) {
        this.title = request.getTitle();
        this.clubName = request.getClubName();
        this.description = request.getDescription();
        this.deadline = request.getDeadline();
        this.sections.addAll(request.createSections());
    }

    public void updateProgress(final Progress progress) {
        this.progress = progress;
    }

    public void updateDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
