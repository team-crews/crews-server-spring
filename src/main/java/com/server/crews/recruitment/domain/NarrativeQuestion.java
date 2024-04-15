package com.server.crews.recruitment.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NarrativeQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Section section;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean necessity;

    @Column(nullable = false)
    private Integer order;

    @Column(nullable = false)
    private Integer wordLimit;

    public void updateSection(final Section section) {
        this.section = section;
    }

    public boolean hasSameSection(final Section section) {
        return this.section.equals(section);
    }
}
