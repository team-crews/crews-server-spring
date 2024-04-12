package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.NarrativeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NarrativeAnswerRepository extends JpaRepository<NarrativeAnswer, Long> {
    boolean existsAllByNarrativeQuestionIdIn(List<Long> questionIds);
    List<NarrativeAnswer> findAllByApplicantId(Long applicantId);
}
