package com.server.crews.recruitment.domain;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "narrative_question",
        indexes = {
                @Index(columnList = "section_id", name = "idx_section_id"),
                @Index(columnList = "recruitment_id", name = "idx_recruitment_id")

        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NarrativeQuestion implements Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Recruitment recruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Section section;

    @Size(max = 250, message = "질문 내용은 250자 이하입니다.")
    @Column(name = "content", nullable = false, length = 250)
    private String content;

    @Column(name = "necessity", nullable = false)
    private Boolean necessity;

    @Column(name = "order_number", nullable = false)
    private Integer order;

    @NotNull(message = "글자 수 제한은 null일 수 없습니다.")
    @Max(value = 1500, message = "글자 수 제한은 최대 1500입니다.")
    @Column(name = "word_limit", nullable = false)
    private Integer wordLimit;

    public NarrativeQuestion(Long id) {
        this(id, null, null, null, null, null, null);
    }

    public NarrativeQuestion(Long id, String content, Boolean necessity, Integer order, Integer wordLimit) {
        this.id = id;
        this.content = content;
        this.necessity = necessity;
        this.order = order;
        this.wordLimit = wordLimit;
    }

    public void updateSection(Section section) {
        this.section = section;
    }

    public void updateRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public Long getSectionId() {
        return this.section.getId();
    }

    public QuestionType getQuestionType() {
        return QuestionType.NARRATIVE;
    }

    @Override
    public boolean isNecessary() {
        return necessity;
    }
}
