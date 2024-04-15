package com.server.crews.recruitment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SelectiveQuestion selectiveQuestion;

    @Column(nullable = false)
    private String content;

    public Choice(String content) {
        this.content = content;
    }

    public void updateSelectiveQuestion(final SelectiveQuestion selectiveQuestion) {
        this.selectiveQuestion = selectiveQuestion;
    }
}
