package com.server.crews.applicant.domain.repository;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NarrativeAnswerRepository extends JpaRepository<NarrativeAnswer, Long> {
    List<NarrativeAnswer> findAllByApplication(Application application);
}
