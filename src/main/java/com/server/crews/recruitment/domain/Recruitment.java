package com.server.crews.recruitment.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "recruitment")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "progress")
    private Progress progress;

    @Column(name = "closing_date")
    private LocalDateTime closingDate;

    @CreatedDate
    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    public Recruitment(String code) { // Todo: null을 최소화할 수 없을까?
        this.code = code;
        this.progress = Progress.IN_PROGRESS;
    }

    public void updateAll(String title, String description, LocalDateTime closingDate, List<Section> sections) {
        this.title = title;
        this.description = description;
        this.closingDate = closingDate;
        addSections(sections);
    }

    public void addSections(List<Section> sections) {
        sections.forEach(section -> section.updateRecruitment(this));
        this.sections.addAll(sections);
    }

    public void updateProgress(final Progress progress) {
        this.progress = progress;
    }

    public void updateClosingDate(final LocalDateTime closingDate) {
        this.closingDate = closingDate;
    }
}
