package com.server.crews.environ.repository;

import com.server.crews.environ.DatabaseCleaner;
import com.server.crews.global.config.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(JpaConfig.class)
public class RepositoryTestConfig {
    @PersistenceContext
    private EntityManager em;

    @Bean
    public TestRepository testRepository() {
        return new TestRepository(em);
    }

    @Bean
    public DatabaseCleaner databaseCleaner() {
        return new DatabaseCleaner(em);
    }
}
