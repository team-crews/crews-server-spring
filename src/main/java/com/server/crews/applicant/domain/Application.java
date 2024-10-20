package com.server.crews.applicant.domain;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.server.crews.auth.domain.Applicant;
import com.server.crews.recruitment.domain.Recruitment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "application",
        indexes = {
                @Index(columnList = "recruitment_id", name = "idx_recruitment_id"),
                @Index(columnList = "applicant_id", name = "idx_applicant_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outcome", nullable = false)
    private Outcome outcome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Applicant applicant;

    @Column(name = "student_number", nullable = false)
    private String studentNumber;

    @Column(name = "major", nullable = false)
    private String major;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NarrativeAnswer> narrativeAnswers;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SelectiveAnswer> selectiveAnswers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "recruitment_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Recruitment recruitment;

    public Application(Long id,
                       Recruitment recruitment,
                       Long applicantId,
                       String studentNumber,
                       String major,
                       String name,
                       List<NarrativeAnswer> narrativeAnswers,
                       List<SelectiveAnswer> selectiveAnswers) {
        this.id = id;
        this.recruitment = recruitment;
        this.applicant = new Applicant(applicantId);
        this.studentNumber = studentNumber;
        this.major = major;
        this.name = name;
        this.outcome = Outcome.PENDING;
        replaceNarrativeAnswers(narrativeAnswers);
        replaceSelectiveAnswers(selectiveAnswers);
    }

    public void replaceNarrativeAnswers(List<NarrativeAnswer> narrativeAnswers) {
        narrativeAnswers.forEach(narrativeAnswer -> narrativeAnswer.updateApplication(this));
        this.narrativeAnswers = new HashSet<>(narrativeAnswers);
    }

    public void replaceSelectiveAnswers(List<SelectiveAnswer> selectiveAnswers) {
        selectiveAnswers.forEach(selectiveAnswer -> selectiveAnswer.updateApplication(this));
        this.selectiveAnswers = new HashSet<>(selectiveAnswers);
    }

    public Map<Long, NarrativeAnswer> getNarrativeAnswersByQuestionId() {
        return this.narrativeAnswers.stream()
                .collect(toMap(NarrativeAnswer::getQuestionId, identity()));
    }

    public Map<Long, List<SelectiveAnswer>> getSelectiveAnswersByQuestionId() {
        return this.selectiveAnswers.stream()
                .collect(groupingBy(SelectiveAnswer::getQuestionId));
    }

    public void pass() {
        this.outcome = Outcome.PASS;
    }

    public void reject() {
        this.outcome = Outcome.FAIL;
    }

    public boolean isNotDetermined() {
        return outcome.equals(Outcome.PENDING);
    }

    public boolean canBeAccessedBy(Long publisherId) {
        return this.recruitment.isPublishedBy(publisherId);
    }
}
