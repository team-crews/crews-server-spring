package com.server.crews.applicant.domain;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "narrative_answer",
        indexes = @Index(columnList = "application_id", name = "idx_application_id")
)
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

    @Size(max = 1500, message = "서술형 답안 내용은 1500자 이하입니다.")
    @Column(name = "content", nullable = false, length = 1500)
    private String content;

    public NarrativeAnswer(Long id, NarrativeQuestion narrativeQuestion, String content) {
        this.id = id;
        this.narrativeQuestion = narrativeQuestion;
        this.content = content;
    }

    public void updateApplication(Application application) {
        this.application = application;
    }

    public void setToOriginalId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return this.narrativeQuestion.getId();
    }
}
