package com.server.crews.environ.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.crews.global.config.JpaConfig;
import com.server.crews.recruitment.repository.RecruitmentDslRepository;
import com.server.crews.recruitment.repository.RecruitmentDslRepositoryImpl;
import com.server.crews.recruitment.repository.SelectiveQuestionDslRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionDslRepositoryImpl;
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
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public RecruitmentDslRepository recruitmentDslRepository() {
        return new RecruitmentDslRepositoryImpl(jpaQueryFactory());
    }

    @Bean
    public SelectiveQuestionDslRepository selectiveQuestionDslRepository() {
        return new SelectiveQuestionDslRepositoryImpl(jpaQueryFactory());
    }

    @Bean
    public TestRepository testRepository() {
        return new TestRepository(em);
    }
}
