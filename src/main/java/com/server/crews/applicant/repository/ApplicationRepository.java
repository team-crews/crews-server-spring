package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationDslRepository {

    @Query("""
            SELECT a from Application a
            WHERE a.applicant.id = :applicantId
            """)
    Optional<Application> findByApplicant(@Param("applicantId") Long applicantId);
}
