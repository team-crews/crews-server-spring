package com.server.crews.recruitment.domain;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "choice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice implements Comparable<Choice> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selective_question_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private SelectiveQuestion selectiveQuestion;

    @Size(max = 50, message = "선택지 내용은 50자 이하입니다.")
    @Column(name = "content", nullable = false, length = 50)
    private String content;

    public Choice(Long id) {
        this(id, null);
    }

    public Choice(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public void updateSelectiveQuestion(SelectiveQuestion selectiveQuestion) {
        this.selectiveQuestion = selectiveQuestion;
    }

    @Override
    public int compareTo(Choice other) {
        return Long.compare(this.id, other.id);
    }
}
