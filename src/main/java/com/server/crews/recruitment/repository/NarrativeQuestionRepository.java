package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Section;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NarrativeQuestionRepository extends JpaRepository<NarrativeQuestion, Long> {
    List<NarrativeQuestion> findAllBySectionIn(List<Section> sections);
}
