package com.server.crews.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @Column(name = "username")
    private String username;

    @Column(nullable = false, name = "token")
    private String token;

    public RefreshToken(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public boolean isSameToken(String token) {
        return this.token.equals(token);
    }
}
