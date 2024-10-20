package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SelectiveQuestionRepository extends JpaRepository<SelectiveQuestion, Long> {
    List<SelectiveQuestion> findAllByIdIn(Set<Long> questionIds);

    @Query("""
            select s from SelectiveQuestion s
            left join fetch s.choices
            where s.section in :sections
            """)
    List<SelectiveQuestion> findAllWithChoicesInSections(@Param("sections") List<Section> sections);

    @Query("""
            select s from SelectiveQuestion s
            where s.recruitment.id = :recruitmentId
            """)
    List<SelectiveQuestion> findAllByRecruitmentId(@Param("recruitmentId") Long recruitmentId);
}
