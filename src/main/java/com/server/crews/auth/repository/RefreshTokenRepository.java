package com.server.crews.auth.repository;

import com.server.crews.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByOwnerId(String id);
    Optional<RefreshToken> findByToken(String token);
}
