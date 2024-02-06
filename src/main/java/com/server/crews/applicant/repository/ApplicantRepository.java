package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Applicant;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicantRepository extends MongoRepository<Applicant, String> {
    Optional<Applicant> findBySecretCode(String code);
}
