package com.server.crews.recruitment.domain;

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
@Table(name = "selective_question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectiveQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Section section;

    @OneToMany(mappedBy = "selectiveQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choices;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "necessity", nullable = false)
    private Boolean necessity;

    @Column(name = "order", nullable = false)
    private Integer order;

    @Column(name = "minimum_selection", nullable = false)
    private Integer minimumSelection;

    @Column(name = "maximum_selection", nullable = false)
    private Integer maximumSelection;

    public SelectiveQuestion(Long id, List<Choice> choices, String content, Boolean necessity, Integer order,
                             Integer minimumSelection, Integer maximumSelection) {
        choices.forEach(choice -> choice.updateSelectiveQuestion(this));
        this.id = id;
        this.choices = new ArrayList<>(choices);
        this.content = content;
        this.necessity = necessity;
        this.order = order;
        this.minimumSelection = minimumSelection;
        this.maximumSelection = maximumSelection;
    }

    public void updateSection(final Section section) {
        this.section = section;
    }

    public void setByExistingId(Long id) {
        this.id = id;
    }
}
