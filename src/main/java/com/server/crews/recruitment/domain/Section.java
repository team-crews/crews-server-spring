package com.server.crews.recruitment.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "section")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Recruitment recruitment;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
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
