package com.server.crews.recruitment.mapper;

import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.QuestionSaveRequest;
import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.request.SectionSaveRequest;
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

    public static Section sectionSaveRequestToSection(SectionSaveRequest sectionSaveRequest) {
        List<QuestionSaveRequest> questionSaveRequests = sectionSaveRequest.questions();
        List<NarrativeQuestion> narrativeQuestions = questionSaveRequests.stream()
                .filter(questionSaveRequest -> QuestionType.NARRATIVE.hasSameName(questionSaveRequest.type()))
                .map(QuestionMapper::questionSaveRequestToNarrativeQuestion)
                .toList();
        List<SelectiveQuestion> selectiveQuestions = questionSaveRequests.stream()
                .filter(questionSaveRequest -> QuestionType.SELECTIVE.hasSameName(questionSaveRequest.type()))
                .map(QuestionMapper::questionSaveRequestToSelectiveQuestion)
                .toList();
        return new Section(
                sectionSaveRequest.id(),
                sectionSaveRequest.name(),
                sectionSaveRequest.description(),
                narrativeQuestions,
                selectiveQuestions);
    }
}
