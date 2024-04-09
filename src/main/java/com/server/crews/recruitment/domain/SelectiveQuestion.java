package com.server.crews.recruitment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectiveQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Section section;

    @OneToMany(mappedBy = "selectiveQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choices;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean necessity;

    @Column(nullable = false)
    private Integer order;

    @Column(nullable = false)
    private Integer minimumSelection;

    @Column(nullable = false)
    private Integer maximumSelection;

    @Builder
    public SelectiveQuestion(
            final List<Choice> choices, final String content,
            final Boolean necessity, final Integer order,
            final Integer minimumSelection, final Integer maximumSelection) {
        this.choices = new ArrayList<>(choices);
        this.content = content;
        this.necessity = necessity;
        this.order = order;
        this.minimumSelection = minimumSelection;
        this.maximumSelection = maximumSelection;
    }
}
