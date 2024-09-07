package com.server.crews.recruitment.util;

import com.server.crews.recruitment.dto.response.QuestionResponse;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import java.util.Comparator;

public class QuestionSorter {

    public static void sort(RecruitmentDetailsResponse recruitmentDetailsResponse) {
        recruitmentDetailsResponse.sections().forEach(QuestionSorter::sortQuestions);
    }

    private static void sortQuestions(SectionResponse sectionResponse) {
        sectionResponse.questions().sort(Comparator.comparing(QuestionResponse::order));
    }
}
