package com.server.crews.auth.repository;

import com.server.crews.auth.domain.Applicant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    Optional<Applicant> findByEmail(String email);
}
