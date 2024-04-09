package com.server.crews.recruitment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.crews.recruitment.domain.QRecruitment;
import com.server.crews.recruitment.domain.QSection;
import com.server.crews.recruitment.domain.Recruitment;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RecruitmentDslRepositoryImpl implements RecruitmentDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Recruitment> findDetailsById(Long id) {
        QRecruitment qRecruitment = QRecruitment.recruitment;
        QSection qSection = QSection.section;

        Recruitment recruitment =  jpaQueryFactory.selectFrom(qRecruitment)
                .leftJoin(qRecruitment.sections, qSection)
                .fetchJoin()
                .where(qRecruitment.id.eq(id))
                .fetchFirst();
        return Optional.ofNullable(recruitment);
    }
}
