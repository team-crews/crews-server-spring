package com.server.crews.application.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "recruitments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application {
    @Id
    private String id;

    @Indexed(unique = true)
    private String secretCode;

    public Application(final String secretCode) {
        this.secretCode = secretCode;
    }
}
