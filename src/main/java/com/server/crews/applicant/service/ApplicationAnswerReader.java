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

    public static ApplicationDetailsResponse readBySection(Recruitment recruitment, Application application) {
        List<Section> orderedSections = recruitment.getOrderedSections();

        Map<Long, AnswerResponse> narrativeAnswerResponsesByQuestionId = narrativeAnswerResponsesByQuestionIdInApplication(
                application);
        Map<Long, AnswerResponse> selectiveAnswerResponsesByQuestionId = selectiveAnswerResponsesByQuestionIdInApplication(
                application);

        List<SectionAnswerResponse> sectionAnswerResponses = orderedSections.stream()
                .map(section -> new SectionAnswerResponse(
                        section.getId(),
                        getAnswerResponsesBySection(section, narrativeAnswerResponsesByQuestionId,
                                selectiveAnswerResponsesByQuestionId)
                ))
                .toList();

        return ApplicationMapper.applicationToApplicationDetailsResponse(application, sectionAnswerResponses);
    }

    private static List<AnswerResponse> getAnswerResponsesBySection(Section section,
                                                                    Map<Long, AnswerResponse> narrativeAnswers,
                                                                    Map<Long, AnswerResponse> selectiveAnswers) {
        return section.getOrderedQuestions().stream()
                .map(question -> getAnswerResponseByType(question, narrativeAnswers, selectiveAnswers))
                .toList();
    }

    private static AnswerResponse getAnswerResponseByType(OrderedQuestion question,
                                                          Map<Long, AnswerResponse> narrativeAnswers,
                                                          Map<Long, AnswerResponse> selectiveAnswers) {
        if (question.getQuestionType() == QuestionType.NARRATIVE) {
            return narrativeAnswers.getOrDefault(question.getId(), getBlankAnswerResponse(question));
        }
        return selectiveAnswers.getOrDefault(question.getId(), getBlankAnswerResponse(question));
    }

    private static AnswerResponse getBlankAnswerResponse(OrderedQuestion question) {
        return new AnswerResponse(question.getId(), null, null, question.getQuestionType());
    }

    private static Map<Long, AnswerResponse> narrativeAnswerResponsesByQuestionIdInApplication(
            Application application) {
        return application.getNarrativeAnswers().stream()
                .map(AnswerMapper::narrativeAnswerToAnswerResponse)
                .collect(toMap(AnswerResponse::questionId, identity()));
    }

    private static Map<Long, AnswerResponse> selectiveAnswerResponsesByQuestionIdInApplication(
            Application application) {
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
