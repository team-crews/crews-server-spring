package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.SelectiveQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SelectiveQuestionRepository extends JpaRepository<SelectiveQuestion, Long>, SelectiveQuestionDslRepository {
    List<SelectiveQuestion> findAllByIdIn(List<Long> questionIds);
}
