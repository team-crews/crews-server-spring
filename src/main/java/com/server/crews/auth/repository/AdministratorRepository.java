package com.server.crews.auth.repository;

import com.server.crews.auth.domain.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByClubName(String clubName);
}
