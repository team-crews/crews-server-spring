package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
