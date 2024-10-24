package com.server.crews.applicant.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.response.AnswerResponse;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.SectionAnswerResponse;
import com.server.crews.applicant.mapper.AnswerMapper;
import com.server.crews.applicant.mapper.ApplicationMapper;
import com.server.crews.recruitment.domain.OrderedQuestion;
import com.server.crews.recruitment.domain.QuestionType;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ApplicationAnswerReader {
    private final List<Section> orderedSections;
    private final Application application;

    public ApplicationAnswerReader(Recruitment recruitment, Application application) {
        this.orderedSections = recruitment.getOrderedSections();
        this.application = application;
    }

    public ApplicationDetailsResponse readBySection() {
        Map<Long, AnswerResponse> narrativeAnswerResponsesByQuestionId = narrativeAnswerResponsesByQuestionIdInApplication();
        Map<Long, AnswerResponse> selectiveAnswerResponsesByQuestionId = selectiveAnswerResponsesByQuestionIdInApplication();

        List<SectionAnswerResponse> sectionAnswerResponses = orderedSections.stream()
                .map(section -> new SectionAnswerResponse(
                        section.getId(),
                        getAnswerResponsesBySection(section, narrativeAnswerResponsesByQuestionId,
                                selectiveAnswerResponsesByQuestionId)
                ))
                .toList();

        return ApplicationMapper.applicationToApplicationDetailsResponse(application, sectionAnswerResponses);
    }

    private List<AnswerResponse> getAnswerResponsesBySection(Section section,
                                                             Map<Long, AnswerResponse> narrativeAnswers,
                                                             Map<Long, AnswerResponse> selectiveAnswers) {
        return section.getOrderedQuestions().stream()
                .map(question -> getAnswerResponseByType(question, narrativeAnswers, selectiveAnswers))
                .toList();
    }

    private AnswerResponse getAnswerResponseByType(OrderedQuestion question,
                                                   Map<Long, AnswerResponse> narrativeAnswers,
                                                   Map<Long, AnswerResponse> selectiveAnswers) {
        if (question.getQuestionType() == QuestionType.NARRATIVE) {
            return narrativeAnswers.getOrDefault(question.getId(), getBlankAnswerResponse(question));
        }
        return selectiveAnswers.getOrDefault(question.getId(), getBlankAnswerResponse(question));
    }

    private AnswerResponse getBlankAnswerResponse(OrderedQuestion question) {
        return new AnswerResponse(question.getId(), null, null, question.getQuestionType());
    }

    private Map<Long, AnswerResponse> narrativeAnswerResponsesByQuestionIdInApplication() {
        return application.getNarrativeAnswers().stream()
                .map(AnswerMapper::narrativeAnswerToAnswerResponse)
                .collect(toMap(AnswerResponse::questionId, identity()));
    }

    private Map<Long, AnswerResponse> selectiveAnswerResponsesByQuestionIdInApplication() {
        return application.getSelectiveAnswersByQuestionId()
                .values()
                .stream()
                .map(selectiveAnswers -> {
                    selectiveAnswers.sort(Comparator.comparingLong(SelectiveAnswer::getChoiceId));
                    return AnswerMapper.selectiveAnswerToAnswerResponse(selectiveAnswers);
                })
                .collect(toMap(AnswerResponse::questionId, identity()));
    }
}
