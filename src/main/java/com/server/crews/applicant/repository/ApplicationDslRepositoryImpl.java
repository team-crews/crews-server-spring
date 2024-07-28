package com.server.crews.applicant.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.QApplication;
import com.server.crews.auth.domain.QApplicant;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ApplicationDslRepositoryImpl implements ApplicationDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Application> findAllWithApplicantByRecruitmentId(Long recruitmentId) {
        QApplication qApplication = QApplication.application;
        QApplicant qApplicant = QApplicant.applicant;
        return jpaQueryFactory.selectFrom(qApplication)
                .innerJoin(qApplication.applicant, qApplicant)
                .fetchJoin()
                .where(qApplicant.recruitment.id.eq(recruitmentId))
                .fetch();
    }
}
