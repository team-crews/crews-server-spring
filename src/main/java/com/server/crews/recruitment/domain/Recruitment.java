package com.server.crews.recruitment.domain;

import com.server.crews.auth.domain.Administrator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "recruitment",
        indexes = {
                @Index(columnList = "publisher_id", name = "idx_publisher_id"),
                @Index(columnList = "code", name = "idx_code")
        }
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "progress", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecruitmentProgress progress;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Administrator publisher;

    @CreatedDate
    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    public Recruitment(Long id, String code, String title, String description, LocalDateTime deadline,
                       Administrator publisher, List<Section> sections) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.publisher = publisher;
        this.progress = RecruitmentProgress.READY;
        addSections(sections);
    }

    public void updateDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void addSections(List<Section> sections) {
        sections.forEach(section -> section.updateRecruitment(this));
        this.sections.addAll(sections);
    }

    public void start() {
        this.progress = RecruitmentProgress.IN_PROGRESS;
    }

    public void announce() {
        this.progress = RecruitmentProgress.ANNOUNCED;
    }

    public void close() {
        this.progress = RecruitmentProgress.COMPLETION;
    }

    public boolean isAnnounced() {
        return this.progress == RecruitmentProgress.ANNOUNCED;
    }

    public boolean isStarted() {
        return this.progress != RecruitmentProgress.READY;
    }

    public boolean hasPassedDeadline(LocalDateTime now) {
        return now.isAfter(deadline) || now.equals(deadline);
    }

    public void sortQuestions() {
        this.sections.forEach(Section::sortQuestions);
    }

    public boolean isPublishedBy(Long publisherId) {
        return this.publisher.getId().equals(publisherId);
    }
}
