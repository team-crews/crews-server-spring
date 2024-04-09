package com.server.crews.recruitment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectiveQuestion implements Question, Comparable<SelectiveQuestion> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Section section;

    @OneToMany(mappedBy = "selective_question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choices = new ArrayList<>();

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

    @Override
    public boolean isNarrative() {
        return false;
    }

    @Override
    public int compareTo(SelectiveQuestion other) {
        return Integer.compare(order, other.order);
    }
}
