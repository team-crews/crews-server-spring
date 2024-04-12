package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NarrativeQuestionRepository extends JpaRepository<NarrativeQuestion, Long> {
    List<NarrativeQuestion> findAllBySectionIn(List<Section> sections);
    boolean existsAllByIdIn(List<Long> questionIds);
}
