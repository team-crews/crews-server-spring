package com.server.crews.applicant.domain;

import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "applicants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Applicant {
    @Id
    private String id;

    @Indexed(unique = true)
    private String secretCode;

    private Outcome outcome;

    private String recruitmentId;

    private Long studentNumber;

    private String email;

    private String name;

    private List<Answer> answers;

    public Applicant(final String secretCode) {
        this.secretCode = secretCode;
        this.outcome = Outcome.PENDING;
    }

    public void updateAll(final ApplicationSaveRequest request) {
        setAnswersOrder(request.answers());

        this.recruitmentId = request.recruitmentId();
        this.studentNumber = request.studentNumber();
        this.email = request.email();
        this.name = request.name();
        this.answers = request.answers();
    }

    public void setAnswersOrder(List<Answer> answersInRequest) {
        int sequence = 1;
        for(Answer answer: answersInRequest) {
            answer.setOrder(sequence);
            sequence += 1;
        }
    }
}
