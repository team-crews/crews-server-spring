package com.server.crews.applicant.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectiveAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicantId;

    @Column(nullable = false)
    private Long choiceId;

    @Column(nullable = false)
    private Long selectiveQuestionId;
}
