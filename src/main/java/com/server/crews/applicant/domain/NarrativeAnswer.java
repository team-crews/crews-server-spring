package com.server.crews.applicant.domain;

import com.server.crews.auth.domain.Applicant;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NarrativeAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "application_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

    @JoinColumn(name = "narrative_question_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private NarrativeQuestion narrativeQuestion;

    @Column(name = "content", nullable = false)
    private String content;

    public NarrativeAnswer(NarrativeQuestion narrativeQuestion, String content) {
        this.narrativeQuestion = narrativeQuestion;
        this.content = content;
    }

    public void updateApplication(Application application) {
        this.application = application;
    }
}
