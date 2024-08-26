package com.server.crews.recruitment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.crews.recruitment.domain.QChoice;
import com.server.crews.recruitment.domain.QSelectiveQuestion;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SelectiveQuestionDslRepositoryImpl implements SelectiveQuestionDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SelectiveQuestion> findAllWithChoicesInSections(List<Section> sections) {
        QSelectiveQuestion qSelectiveQuestion = QSelectiveQuestion.selectiveQuestion;
        QChoice qChoice = QChoice.choice;

        return jpaQueryFactory.selectFrom(qSelectiveQuestion)
                .leftJoin(qSelectiveQuestion.choices, qChoice)
                .fetchJoin()
                .where(qSelectiveQuestion.section.in(sections))
                .fetch();
    }
}
