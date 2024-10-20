package com.server.crews.recruitment.domain;

import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "selective_question",
        indexes = {
                @Index(columnList = "section_id", name = "idx_section_id"),
                @Index(columnList = "recruitment_id", name = "idx_recruitment_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectiveQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Recruitment recruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Section section;

    @OneToMany(mappedBy = "selectiveQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choices;

    @Size(max = 250, message = "질문 내용은 250자 이하입니다.")
    @Column(name = "content", nullable = false, length = 250)
    private String content;

    @Column(name = "necessity", nullable = false)
    private Boolean necessity;

    @Column(name = "order_number", nullable = false)
    private Integer order;

    @NotNull(message = "최소 선택 개수는 null일 수 없습니다.")
    @Min(value = 1, message = "최소 선택 개수는 1개 이상입니다.")
    @Max(value = 10, message = "최소 선택 개수는 10개 이하입니다.")
    @Column(name = "minimum_selection", nullable = false)
    private Integer minimumSelection;

    @NotNull(message = "최대 선택 개수는 null일 수 없습니다.")
    @Min(value = 1, message = "최대 선택 개수는 1개 이상입니다.")
    @Max(value = 10, message = "최대 선택 개수는 10개 이하입니다.")
    @Column(name = "maximum_selection", nullable = false)
    private Integer maximumSelection;

    public SelectiveQuestion(Long id, List<Choice> choices, String content, Boolean necessity, Integer order,
                             Integer minimumSelection, Integer maximumSelection) {
        validateSelectionCount(minimumSelection, maximumSelection);
        choices.forEach(choice -> choice.updateSelectiveQuestion(this));
        this.id = id;
        this.choices = new ArrayList<>(choices);
        this.content = content;
        this.necessity = necessity;
        this.order = order;
        this.minimumSelection = minimumSelection;
        this.maximumSelection = maximumSelection;
    }

    private void validateSelectionCount(Integer minimumSelection, Integer maximumSelection) {
        if (minimumSelection == null || maximumSelection == null) {
            return;
        }
        if (minimumSelection > maximumSelection) {
            throw new CrewsException(CrewsErrorCode.INVALID_SELECTION_COUNT);
        }
    }

    public void updateSection(Section section) {
        this.section = section;
    }

    public void updateRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }
}
