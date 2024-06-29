package com.server.crews.applicant.domain;

import com.server.crews.auth.domain.Applicant;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Applicant applicant;

    @Column(name = "student_number", nullable = false)
    private String studentNumber;

    @Column(name = "major", nullable = false)
    private String major;

    @Column(name = "name", nullable = false)
    private String name;

    public Application(Applicant applicant, String studentNumber, String major, String name) {
        this.applicant = applicant;
        this.studentNumber = studentNumber;
        this.major = major;
        this.name = name;
        this.outcome = Outcome.PENDING;
    }

    public void decideOutcome(Outcome outcome) {
        this.outcome = outcome;
    }

    public boolean isNotDetermined() {
        return outcome.equals(Outcome.PENDING);
    }
}
