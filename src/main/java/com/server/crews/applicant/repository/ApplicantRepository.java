package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, String> {
    Optional<Applicant> findBySecretCode(String code);
    List<Applicant> findAllByRecruitmentId(String id);
}
