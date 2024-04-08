package com.server.crews.recruitment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectiveQuestion implements Question, Comparable<SelectiveQuestion> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sectionId;

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
