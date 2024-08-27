package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.SelectiveAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectiveAnswerRepository extends JpaRepository<SelectiveAnswer, Long> {
    List<SelectiveAnswer> findAllByApplication(Application application);
}
