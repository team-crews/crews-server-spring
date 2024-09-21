package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Choice;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findAllByIdIn(Set<Long> ids);
}
