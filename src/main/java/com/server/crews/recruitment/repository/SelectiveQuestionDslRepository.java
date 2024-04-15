package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;

import java.util.List;

public interface SelectiveQuestionDslRepository {
    List<SelectiveQuestion> findAllWithChoicesInSections(List<Section> sections);
}

