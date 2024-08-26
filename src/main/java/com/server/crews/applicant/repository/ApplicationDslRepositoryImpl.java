package com.server.crews.applicant.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.QApplication;
import com.server.crews.recruitment.domain.QRecruitment;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationDslRepositoryImpl implements ApplicationDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Application> findAllWithApplicantByPublisherId(Long publisherId) {
        QApplication qApplication = QApplication.application;
        QRecruitment qRecruitment = QRecruitment.recruitment;
        return jpaQueryFactory.selectFrom(qApplication)
                .innerJoin(qApplication.recruitment, qRecruitment)
                .fetchJoin()
                .where(qRecruitment.publisher.id.eq(publisherId))
                .fetch();
    }
}
