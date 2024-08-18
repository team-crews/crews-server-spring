package com.server.crews.applicant.domain;

import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "selective_answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectiveAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "application_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

    @JoinColumn(name = "choice_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Choice choice;

    @JoinColumn(name = "selective_question_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private SelectiveQuestion selectiveQuestion;

    public SelectiveAnswer(Long id, Choice choice, SelectiveQuestion selectiveQuestion) {
        this.id = id;
        this.choice = choice;
        this.selectiveQuestion = selectiveQuestion;
    }

    public void updateApplication(Application application) {
        this.application = application;
    }
}
