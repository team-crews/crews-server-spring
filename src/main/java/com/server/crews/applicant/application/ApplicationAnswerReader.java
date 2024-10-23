package com.server.crews.applicant.application;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.dto.response.AnswerResponse;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.SectionAnswerResponse;
import com.server.crews.applicant.util.AnswerMapper;
import com.server.crews.applicant.util.ApplicationMapper;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.OrderedQuestion;
import com.server.crews.recruitment.domain.QuestionType;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplicationAnswerReader {
    private final LinkedHashMap<Long, List<OrderedQuestion>> orderedQuestionsByOrderedSectionId;
    private final Application application;

    public ApplicationAnswerReader(List<NarrativeQuestion> narrativeQuestions,
                                   List<SelectiveQuestion> selectiveQuestions,
                                   Application application) {
        this.orderedQuestionsByOrderedSectionId = orderedQuestionsFrom(narrativeQuestions, selectiveQuestions);
        this.application = application;
    }

    private LinkedHashMap<Long, List<OrderedQuestion>> orderedQuestionsFrom(List<NarrativeQuestion> narrativeQuestions,
                                                                            List<SelectiveQuestion> selectiveQuestions) {
        Map<Long, List<OrderedQuestion>> narrativeQuestionsBySectionId = groupBySectionId(narrativeQuestions);
        Map<Long, List<OrderedQuestion>> selectiveQuestionsBySectionId = groupBySectionId(selectiveQuestions);

        List<Long> orderedSectionIds = getOrderedSectionIds(narrativeQuestionsBySectionId.keySet(),
                selectiveQuestionsBySectionId.keySet());

        LinkedHashMap<Long, List<OrderedQuestion>> orderedQuestionsBySectionId = new LinkedHashMap<>();
        for (Long sectionId : orderedSectionIds) {
            List<OrderedQuestion> narrativeOrderedQuestions = narrativeQuestionsBySectionId.getOrDefault(sectionId,
                    Collections.emptyList());
            List<OrderedQuestion> selectiveOrderedQuestions = selectiveQuestionsBySectionId.getOrDefault(sectionId,
                    Collections.emptyList());

            List<OrderedQuestion> orderedQuestions = new ArrayList<>(narrativeOrderedQuestions);
            orderedQuestions.addAll(selectiveOrderedQuestions);
            Collections.sort(orderedQuestions);

            orderedQuestionsBySectionId.put(sectionId, orderedQuestions);
        }

        return orderedQuestionsBySectionId;
    }

    private <T extends OrderedQuestion> Map<Long, List<OrderedQuestion>> groupBySectionId(List<T> questions) {
        return questions.stream()
                .collect(groupingBy(OrderedQuestion::getSectionId));
    }

    private List<Long> getOrderedSectionIds(Set<Long> sectionIdsInNarrativeQuestions,
                                            Set<Long> sectionIdsInSelectiveQuestions) {
        Set<Long> sectionIds = new HashSet<>(sectionIdsInNarrativeQuestions);
        sectionIds.addAll(sectionIdsInSelectiveQuestions);

        List<Long> sortedSectionIds = new ArrayList<>(sectionIds);
        Collections.sort(sortedSectionIds);

        return sortedSectionIds;
    }

    public ApplicationDetailsResponse readBySection() {
        Map<Long, AnswerResponse> narrativeAnswerResponsesByQuestionId = narrativeAnswerResponsesByQuestionIdInApplication();
        Map<Long, AnswerResponse> selectiveAnswerResponsesByQuestionId = selectiveAnswerResponsesByQuestionIdInApplication();

        List<SectionAnswerResponse> sectionAnswerResponse = new ArrayList<>();
        for (Map.Entry<Long, List<OrderedQuestion>> entry : orderedQuestionsByOrderedSectionId.entrySet()) {
            List<AnswerResponse> answerResponses = new ArrayList<>();

            for (OrderedQuestion question : entry.getValue()) {
                if (question.getQuestionType() == QuestionType.NARRATIVE) {
                    AnswerResponse answerResponse = getAnswerResponse(narrativeAnswerResponsesByQuestionId, question);
                    answerResponses.add(answerResponse);
                }
                if (question.getQuestionType() == QuestionType.SELECTIVE) {
                    AnswerResponse answerResponse = getAnswerResponse(selectiveAnswerResponsesByQuestionId, question);
                    answerResponses.add(answerResponse);
                }
            }

            sectionAnswerResponse.add(new SectionAnswerResponse(entry.getKey(), answerResponses));
        }

        return ApplicationMapper.applicationToApplicationDetailsResponse(application, sectionAnswerResponse);
    }

    private Map<Long, AnswerResponse> narrativeAnswerResponsesByQuestionIdInApplication() {
        return application.getNarrativeAnswers().stream()
                .map(AnswerMapper::narrativeAnswerToAnswerResponse)
                .collect(toMap(AnswerResponse::questionId, identity()));
    }

    private Map<Long, AnswerResponse> selectiveAnswerResponsesByQuestionIdInApplication() {
        return application.getSelectiveAnswersByQuestionId()
                .entrySet()
                .stream()
                .map(entry -> AnswerMapper.selectiveAnswerToAnswerResponse(entry.getValue()))
                .collect(toMap(AnswerResponse::questionId, identity()));
    }

    private AnswerResponse getAnswerResponse(Map<Long, AnswerResponse> answerResponsesByQuestionId,
                                             OrderedQuestion question) {
        Long questionId = question.getId();
        if (answerResponsesByQuestionId.containsKey(questionId)) {
            return answerResponsesByQuestionId.get(questionId);
        }
        return new AnswerResponse(questionId, null, null, question.getQuestionType());
    }
}
