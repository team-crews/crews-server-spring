package com.server.crews.applicant.application;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.dto.response.AnswerResponse;
import com.server.crews.applicant.dto.response.ApplicationDetailsResponse;
import com.server.crews.applicant.dto.response.SectionAnswerResponse;
import com.server.crews.applicant.util.AnswerMapper;
import com.server.crews.applicant.util.ApplicationMapper;
import com.server.crews.recruitment.domain.OrderedQuestion;
import com.server.crews.recruitment.domain.QuestionType;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import java.util.ArrayList;
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

        List<SectionAnswerResponse> sectionAnswerResponse = new ArrayList<>();
        for (Section section : orderedSections) {
            List<OrderedQuestion> orderedQuestions = section.getOrderedQuestions();
            List<AnswerResponse> answerResponses = new ArrayList<>();

            for (OrderedQuestion question : orderedQuestions) {
                if (question.getQuestionType() == QuestionType.NARRATIVE) {
                    AnswerResponse answerResponse = getAnswerResponse(narrativeAnswerResponsesByQuestionId, question);
                    answerResponses.add(answerResponse);
                }
                if (question.getQuestionType() == QuestionType.SELECTIVE) {
                    AnswerResponse answerResponse = getAnswerResponse(selectiveAnswerResponsesByQuestionId, question);
                    answerResponses.add(answerResponse);
                }
            }

            Long sectionId = section.getId();
            sectionAnswerResponse.add(new SectionAnswerResponse(sectionId, answerResponses));
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
                .values()
                .stream()
                .map(selectiveAnswers -> {
                    selectiveAnswers.sort(Comparator.comparingLong(SelectiveAnswer::getChoiceId));
                    return AnswerMapper.selectiveAnswerToAnswerResponse(selectiveAnswers);
                })
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
