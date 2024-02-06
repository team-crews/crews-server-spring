package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Recruitment;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecruitmentRepository extends MongoRepository<Recruitment, String> {
    Optional<Recruitment> findBySecretCode(String code);
}
