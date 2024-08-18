package com.server.crews.applicant.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.QApplication;
import com.server.crews.auth.domain.QApplicant;
import com.server.crews.recruitment.domain.QRecruitment;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ApplicationDslRepositoryImpl implements ApplicationDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Application> findAllWithApplicantByPublisherId(Long publisherId) {
        QApplication qApplication = QApplication.application;
        QApplicant qApplicant = QApplicant.applicant;
        QRecruitment qRecruitment = QRecruitment.recruitment;
        return jpaQueryFactory.selectFrom(qApplication)
                .innerJoin(qApplication.applicant, qApplicant)
                .fetchJoin()
                .innerJoin(qApplicant.recruitment, qRecruitment)
                .fetchJoin()
                .where(qRecruitment.publisher.id.eq(publisherId))
                .fetch();
    }
}
