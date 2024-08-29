package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Application;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationDslRepository {
    @Query("""
            select count(application) from Application application
            join application.recruitment recruitment
            where recruitment = :recruitment
            """)
    int countAllByRecruitment(@Param("recruitment") Recruitment recruitment);

    @Query("""
            select application from Application application
            join fetch application.applicant
            where application.recruitment = :recruitment
            """)
    List<Application> findAllByRecruitmentWithApplicant(@Param("recruitment") Recruitment recruitment);

    @Query("""
            select a from Application a
            where a.applicant.id = :applicantId
            """)
    Optional<Application> findByApplicantId(@Param("applicantId") Long applicantId);

    @Query("""
            select a from Application a
            join fetch a.recruitment r
            join fetch r.publisher
            where a.id = :id
            """)
    Optional<Application> findByIdWithRecruitmentAndPublisher(@Param("id") Long id);

    @Query("""
            select a from Application a
            join fetch a.recruitment r
            where a.applicant.id = :applicantId and r.code = :recruitmentCode
            """)
    Optional<Application> findByApplicantIdAndRecruitmentCode(@Param("applicantId") Long applicantId,
                                                              @Param("recruitmentCode") String recruitmentCode);
}
