package com.server.crews.auth.repository;

import com.server.crews.auth.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUsername(String username);

    Optional<RefreshToken> findByUsername(String username);
}
