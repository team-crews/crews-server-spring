package com.server.crews.recruitment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Recruitment recruitment;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NarrativeQuestion> narrativeQuestions;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectiveQuestion> selectiveQuestions;

    @Builder
    public Section(
            final String name, final String description,
            final List<NarrativeQuestion> narrativeQuestions,
            final List<SelectiveQuestion> selectiveQuestions) {
        this.name = name;
        this.description = description;
        replaceQuestions(narrativeQuestions, selectiveQuestions);
    }

    public void replaceQuestions(
            final List<NarrativeQuestion> narrativeQuestions,
            final List<SelectiveQuestion> selectiveQuestions) {
        if (narrativeQuestions != null) {
            narrativeQuestions.forEach(narrativeQuestion -> narrativeQuestion.updateSection(this));
            this.narrativeQuestions = new ArrayList<>(narrativeQuestions);
        }
        if (selectiveQuestions != null) {
            selectiveQuestions.forEach(selectiveQuestion -> selectiveQuestion.updateSection(this));
            this.selectiveQuestions = new ArrayList<>(selectiveQuestions);
        }
    }

    public void updateRecruitment(final Recruitment recruitment) {
        this.recruitment = recruitment;
    }
}
