package com.server.crews.applicant.domain;

import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.domain.Role;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "application")
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
    private List<NarrativeAnswer> narrativeAnswers = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectiveAnswer> selectiveAnswers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "recruitment_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Recruitment recruitment;

    public Application(Long id,
                       Recruitment recruitment,
                       Applicant applicant,
                       String studentNumber,
                       String major,
                       String name,
                       List<NarrativeAnswer> narrativeAnswers,
                       List<SelectiveAnswer> selectiveAnswers) {
        this.id = id;
        this.recruitment = recruitment;
        this.applicant = applicant;
        this.studentNumber = studentNumber;
        this.major = major;
        this.name = name;
        updateNarrativeAnswers(narrativeAnswers);
        updateSelectiveAnswers(selectiveAnswers);
        this.outcome = Outcome.PENDING;
    }

    public void updateNarrativeAnswers(List<NarrativeAnswer> narrativeAnswers) {
        this.narrativeAnswers.addAll(narrativeAnswers);
        narrativeAnswers.forEach(narrativeAnswer -> narrativeAnswer.updateApplication(this));
    }

    public void updateSelectiveAnswers(List<SelectiveAnswer> selectiveAnswers) {
        this.selectiveAnswers.addAll(selectiveAnswers);
        selectiveAnswers.forEach(selectiveAnswer -> selectiveAnswer.updateApplication(this));
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
