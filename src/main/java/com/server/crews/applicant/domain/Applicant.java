package com.server.crews.applicant.domain;

import com.server.crews.applicant.dto.request.ApplicationSaveRequest;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import java.util.List;
import java.util.regex.Pattern;
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
    private static final String EMAIL_PATTERN = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";

    @Id
    private String id;

    @Indexed(unique = true)
    private String secretCode;

    private Outcome outcome;

    private String recruitmentId;

    private Long studentNumber;

    private String major;

    private String email;

    private String name;

    private List<Answer> answers;

    public Applicant(final String secretCode) {
        this.secretCode = secretCode;
        this.outcome = Outcome.PENDING;
    }

    public void updateAll(final ApplicationSaveRequest request) {
        validateEmail(request.email());
        setAnswersOrder(request.answers());

        this.recruitmentId = request.recruitmentId();
        this.studentNumber = request.studentNumber();
        this.major = request.major();
        this.email = request.email();
        this.name = request.name();
        this.answers = request.answers();
    }

    private void setAnswersOrder(final List<Answer> answersInRequest) {
        int sequence = 1;
        for(Answer answer: answersInRequest) {
            answer.setOrder(sequence);
            sequence += 1;
        }
    }

    private void validateEmail(final String email) {
        if(!Pattern.matches(EMAIL_PATTERN, email)) {
            throw new CrewsException(ErrorCode.INVALID_EMAIL_PATTERN);
        }
    }

    public void decideOutcome(final Outcome outcome) {
        this.outcome = outcome;
    }
}
