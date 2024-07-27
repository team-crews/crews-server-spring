package com.server.crews.applicant.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.QApplication;
import com.server.crews.auth.domain.QApplicant;
import com.server.crews.recruitment.domain.Recruitment;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ApplicationDslRepositoryImpl implements ApplicationDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Application> findAllByRecruitmentId(Long id) {
        QApplication qApplication = QApplication.application;
        QApplicant qApplicant = QApplicant.applicant;
        return jpaQueryFactory.selectFrom(qApplication)
                .fetchJoin()
                .fetch();
    }

    @Override
    public List<Application> findAllWithApplicantByRecruitment(Recruitment recruitment) {
        QApplication qApplication = QApplication.application;
        QApplicant qApplicant = QApplicant.applicant;
        return jpaQueryFactory.selectFrom(qApplication)
                .innerJoin(qApplication.applicant, qApplicant)
                .fetchJoin()
                .where(qApplicant.recruitment.eq(recruitment))
                .fetch();
    }
}
