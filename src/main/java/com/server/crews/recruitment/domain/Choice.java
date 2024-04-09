package com.server.crews.recruitment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

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

    public Choice(SelectiveQuestion selectiveQuestion, String content) {
        this.selectiveQuestion = selectiveQuestion;
        this.content = content;
    }
}
