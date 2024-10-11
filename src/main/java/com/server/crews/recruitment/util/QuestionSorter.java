package com.server.crews.recruitment.util;

import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.response.ChoiceResponse;
import com.server.crews.recruitment.dto.response.QuestionResponse;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import java.util.Comparator;
import java.util.List;

public class QuestionSorter {

    public static void sort(RecruitmentDetailsResponse recruitmentDetailsResponse) {
        recruitmentDetailsResponse.sections()
                .forEach(sectionResponse -> sortQuestions(sectionResponse.questions()));
    }

    private static void sortQuestions(List<QuestionResponse> questions) {
        questions.stream()
                .filter(questionResponse -> questionResponse.type() == QuestionType.SELECTIVE)
                .forEach(questionResponse -> sortChoices(questionResponse.choices()));
        questions.sort(Comparator.comparing(QuestionResponse::order));
    }

    private static void sortChoices(List<ChoiceResponse> choiceResponses) {
        choiceResponses.sort(Comparator.comparing(ChoiceResponse::id));
    }
}
