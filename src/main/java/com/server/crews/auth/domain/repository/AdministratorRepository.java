package com.server.crews.auth.domain.repository;

import com.server.crews.auth.domain.Administrator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByClubName(String clubName);
}
