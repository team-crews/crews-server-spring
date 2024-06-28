package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long>, RecruitmentDslRepository {
    Optional<Recruitment> findByCode(String code);
}
