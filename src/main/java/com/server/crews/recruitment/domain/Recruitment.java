package com.server.crews.recruitment.domain;

import static java.util.stream.Collectors.groupingBy;

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
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
                @Index(columnList = "code", name = "idx_code"),
                @Index(columnList = "title", name = "idx_title")
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

    @Column(name = "code", nullable = false, columnDefinition = "CHAR(36)")
    private String code;

    @Size(max = 30, message = "모집 공고 제목은 30자 이하입니다.")
    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Size(max = 1500, message = "모집 공고 내용은 1500자 이하입니다.")
    @Column(name = "description", length = 1500)
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

    private void addSections(List<Section> sections) {
        sections.forEach(section -> section.updateRecruitment(this));
        sections.stream()
                .map(section -> section.getSelectiveQuestions())
                .flatMap(Collection::stream)
                .forEach(selectiveQuestion -> selectiveQuestion.updateRecruitment(this));
        sections.stream()
                .map(section -> section.getNarrativeQuestions())
                .flatMap(Collection::stream)
                .forEach(narrativeQuestion -> narrativeQuestion.updateRecruitment(this));
        this.sections.addAll(sections);
    }

    public void updateDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void replaceQuestionsWithFetchedData(List<NarrativeQuestion> narrativeQuestions,
                                                List<SelectiveQuestion> selectiveQuestions) {
        Map<Long, List<NarrativeQuestion>> narrativeQuestionsBySectionId = narrativeQuestions.stream()
                .collect(groupingBy(NarrativeQuestion::getSectionId));
        Map<Long, List<SelectiveQuestion>> selectiveQuestionsBySectionId = selectiveQuestions.stream()
                .collect(groupingBy(SelectiveQuestion::getSectionId));

        sections.forEach(section ->
                section.replaceQuestions(
                        narrativeQuestionsBySectionId.getOrDefault(section.getId(), List.of()),
                        selectiveQuestionsBySectionId.getOrDefault(section.getId(), List.of())
                )
        );
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

    public boolean isInProgress() {
        return this.progress == RecruitmentProgress.IN_PROGRESS;
    }

    public boolean hasOnOrAfterDeadline(LocalDateTime other) {
        return other.isAfter(deadline) || other.equals(deadline);
    }

    public boolean isPublishedBy(Long publisherId) {
        return this.publisher.getId().equals(publisherId);
    }

    public List<Section> getOrderedSections() {
        List<Section> sortedSections = new ArrayList<>(sections);
        sortedSections.sort(Comparator.comparingLong(Section::getId));
        return sortedSections;
    }

    public List<NarrativeQuestion> getNarrativeQuestion() {
        return this.sections.stream()
                .map(Section::getNarrativeQuestions)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<SelectiveQuestion> getSelectiveQuestions() {
        return this.sections.stream()
                .map(Section::getSelectiveQuestions)
                .flatMap(Collection::stream)
                .toList();
    }
}
