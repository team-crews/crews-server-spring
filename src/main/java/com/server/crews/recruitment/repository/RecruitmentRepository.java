package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Recruitment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long>, RecruitmentDslRepository {
    Optional<Recruitment> findByCode(String code);

    @Query("""
            SELECT r FROM Recruitment r
            WHERE r.publisher.id = :publisherId
            """)
    Optional<Recruitment> findByPublisher(@Param("publisherId") Long publisherId);

    @Query("""
            SELECT r FROM Recruitment r
            join fetch r.publisher p
            WHERE p.id = :publisherId
            """)
    Optional<Recruitment> findWithPublisherByPublisher(@Param("publisherId") Long publisherId);
}
