package com.server.crews.auth.repository;

import com.server.crews.auth.domain.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    void deleteByOwnerId(String id);
}
