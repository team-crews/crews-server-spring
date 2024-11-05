package com.server.crews.auth.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("refresh_token")
public class RefreshToken {
    @Id
    private String username;

    @TimeToLive
    private Long validity;

    private String token;

    public RefreshToken(String username, Long validity, String token) {
        this.username = username;
        this.validity = validity;
        this.token = token;
    }

    public boolean isSameToken(String token) {
        return this.token.equals(token);
    }
}
