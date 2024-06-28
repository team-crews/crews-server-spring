package com.server.crews.applicant.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.QApplication;
import com.server.crews.auth.domain.QMember;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ApplicationDslRepositoryImpl implements ApplicationDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Application> findAllByRecruitmentId(Long id) {
        QMember qMember = QMember.member;
        QApplication qApplication = QApplication.application;
        return jpaQueryFactory.selectFrom(qApplication)
                .innerJoin(qApplication.member, qMember)
                .fetchJoin()
                .where(qMember.recruitment.id.eq(id))
                .fetch();
    }
}
