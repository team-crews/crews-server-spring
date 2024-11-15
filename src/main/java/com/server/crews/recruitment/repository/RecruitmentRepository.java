package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.RecruitmentProgress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query("""
            select r from Recruitment r
            where r.publisher.id = :publisherId
            """)
    Optional<Recruitment> findByPublisher(@Param("publisherId") Long publisherId);

    @Query("""
            select r from Recruitment r
            join fetch r.publisher p
            where p.id = :publisherId
            """)
    Optional<Recruitment> findWithPublisherByPublisher(@Param("publisherId") Long publisherId);

    @Query("""
            select r from Recruitment r
            left join fetch r.sections
            where r.publisher.id = :publisherId
            """)
    Optional<Recruitment> findWithSectionsByPublisherId(@Param("publisherId") Long publisherId);

    @Query("""
            select r from Recruitment r
            left join fetch r.sections
            where r.code = :code
            """)
    Optional<Recruitment> findWithSectionsByCode(@Param("code") String code);

    @Query("""
            select r from Recruitment r
            left join fetch r.sections
            where r.title = :title
            """)
    Optional<Recruitment> findWithSectionsByTitle(@Param("title") String title);

    List<Recruitment> findByDeadlineLessThanEqualAndProgressNot(LocalDateTime deadline, RecruitmentProgress progress);
}
