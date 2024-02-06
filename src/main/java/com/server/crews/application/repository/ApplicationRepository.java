package com.server.crews.application.repository;

import com.server.crews.application.domain.Application;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationRepository extends MongoRepository<Application, String> {
    Optional<Application> findBySecretCode(String code);
}
