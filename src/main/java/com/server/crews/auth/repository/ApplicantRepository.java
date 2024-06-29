package com.server.crews.auth.repository;

import com.server.crews.auth.domain.Applicant;
import com.server.crews.recruitment.domain.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    @Query("""
            SELECT a FROM Applicant a
            WHERE a.email = :email AND a.recruitment = :recruitment
             """)
    Optional<Applicant> findByEmailAndRecruitment(@Param("email") String email, @Param("recruitment") Recruitment recruitment);

    Optional<Applicant> findByEmail(String email);
}
