package com.server.crews.auth.repository;

import com.server.crews.auth.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByOwnerId(Long id);

    Optional<RefreshToken> findByToken(String token);
}
