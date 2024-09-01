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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "narrative_question",
        indexes = @Index(columnList = "section_id", name = "idx_section_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NarrativeQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Section section;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "necessity", nullable = false)
    private Boolean necessity;

    @Column(name = "order", nullable = false)
    private Integer order;

    @Column(name = "word_limit", nullable = false)
    private Integer wordLimit;

    public NarrativeQuestion(Long id, String content, Boolean necessity, Integer order, Integer wordLimit) {
        this.id = id;
        this.content = content;
        this.necessity = necessity;
        this.order = order;
        this.wordLimit = wordLimit;
    }

    public void updateSection(final Section section) {
        this.section = section;
    }
}
