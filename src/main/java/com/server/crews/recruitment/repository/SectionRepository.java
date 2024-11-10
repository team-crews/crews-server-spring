package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Section;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query("""
            select s from Section s
            left join fetch s.narrativeQuestions
            left join fetch s.selectiveQuestions
            where s.recruitment.id = :recruitmentId
            """)
    List<Section> findAllWithQuestionsByRecruitmentId(@Param("recruitmentId") Long recruitmentId);
}
