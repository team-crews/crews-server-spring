package com.server.crews.auth.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "refreshTokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    private String id;

    private String token;

    private String ownerId;

    public RefreshToken(final String token, final String ownerId) {
        this.token = token;
        this.ownerId = ownerId;
    }
}
