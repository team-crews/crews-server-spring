package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Section;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NarrativeQuestionRepository extends JpaRepository<NarrativeQuestion, Long> {
    List<NarrativeQuestion> findAllBySectionIn(List<Section> sections);

    List<NarrativeQuestion> findAllByIdIn(Set<Long> questionIds);

    @Query("""
            select n from NarrativeQuestion n
            where n.recruitment.id = :recruitmentId
            """)
    List<NarrativeQuestion> findAllByRecruitmentId(Long recruitmentId);
}
