package com.server.crews.recruitment.util;

import com.server.crews.recruitment.dto.request.QuestionType;
import com.server.crews.recruitment.dto.response.ChoiceResponse;
import com.server.crews.recruitment.dto.response.QuestionResponse;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionSorter {

    public static void sort(RecruitmentDetailsResponse recruitmentDetailsResponse) {
        recruitmentDetailsResponse.sections().forEach(QuestionSorter::sortQuestions);
    }

    private static void sortQuestions(SectionResponse sectionResponse) {
        List<QuestionResponse> questions = sectionResponse.questions();
        List<ChoiceResponse> choices = questions.stream()
                .filter(questionResponse -> questionResponse.type() == QuestionType.SELECTIVE)
                .map(QuestionResponse::choices)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        choices.sort(Comparator.comparing(ChoiceResponse::id));
        questions.sort(Comparator.comparing(QuestionResponse::order));

    }
}
