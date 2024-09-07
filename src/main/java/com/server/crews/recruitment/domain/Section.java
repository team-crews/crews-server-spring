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
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "section")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Recruitment recruitment;

    @Size(max = 50, message = "섹션 이름은 50자 이하입니다.")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 250, message = "섹션 설명은 250자 이하입니다.")
    @Column(name = "description", length = 250)
    private String description;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NarrativeQuestion> narrativeQuestions;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectiveQuestion> selectiveQuestions;

    public Section(Long id, String name, String description, List<NarrativeQuestion> narrativeQuestions,
                   List<SelectiveQuestion> selectiveQuestions) {
        this.id = id;
        this.name = name;
        this.description = description;
        replaceQuestions(narrativeQuestions, selectiveQuestions);
    }

    public void replaceQuestions(List<NarrativeQuestion> narrativeQuestions,
                                 List<SelectiveQuestion> selectiveQuestions) {
        narrativeQuestions.forEach(narrativeQuestion -> narrativeQuestion.updateSection(this));
        this.narrativeQuestions = new ArrayList<>(narrativeQuestions);
        selectiveQuestions.forEach(selectiveQuestion -> selectiveQuestion.updateSection(this));
        this.selectiveQuestions = new ArrayList<>(selectiveQuestions);
    }

    public void updateRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public void sortQuestions() {
        this.narrativeQuestions.sort(Comparator.comparingInt(NarrativeQuestion::getOrder));
        this.selectiveQuestions.sort(Comparator.comparingInt(SelectiveQuestion::getOrder));
    }
}
