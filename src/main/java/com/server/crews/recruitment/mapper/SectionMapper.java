package com.server.crews.recruitment.mapper;

import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.dto.response.QuestionResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import java.util.ArrayList;
import java.util.List;

public class SectionMapper {

    public static SectionResponse sectionToSectionResponse(Section section) {
        List<QuestionResponse> selectiveQuestionResponses = section.getSelectiveQuestions().stream()
                .map(QuestionMapper::selectiveQuestionToQuestionResponse)
                .toList();
        List<QuestionResponse> narrativeQuestionResponses = section.getNarrativeQuestions().stream()
                .map(QuestionMapper::narrativeQuestionToQuestionResponse)
                .toList();
        List<QuestionResponse> allQuestionResponses = new ArrayList<>(selectiveQuestionResponses);
        allQuestionResponses.addAll(narrativeQuestionResponses);
        return SectionResponse.builder()
                .id(section.getId())
                .name(section.getName())
                .description(section.getDescription())
                .questions(allQuestionResponses)
                .build();
    }
}
