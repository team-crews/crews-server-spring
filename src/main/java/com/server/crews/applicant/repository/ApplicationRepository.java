package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationDslRepository {
}
