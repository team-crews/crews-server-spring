package com.server.crews.application.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "applications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application {
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

    public Application(final String secretCode) {
        this.secretCode = secretCode;
        this.outcome = Outcome.PENDING;
    }
}
