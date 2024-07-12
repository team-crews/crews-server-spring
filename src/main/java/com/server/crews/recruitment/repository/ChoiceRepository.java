package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findAllByIdIn(List<Long> ids);
}
