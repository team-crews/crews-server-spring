package com.server.crews.fixture;

import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.ChoiceSaveRequest;
import java.util.List;

public class QuestionFixture {
    public static final String INTRODUCTION_QUESTION = "자기소개해주세요";
    public static final String STRENGTH_QUESTION = "장점을 골라주세요";
    public static final String FAITHFUL_CHOICE = "성실함";
    public static final String BRIGHT_CHOICE = "밝음";
    public static final String METICULOUS_CHOICE = "꼼꼼함";
    public static final List<ChoiceSaveRequest> STRENGTH_CHOICES_REQUEST = List.of(
            new ChoiceSaveRequest(null, FAITHFUL_CHOICE),
            new ChoiceSaveRequest(null, BRIGHT_CHOICE),
            new ChoiceSaveRequest(null, METICULOUS_CHOICE)
    );

    public static List<Choice> CHOICES() {
        return List.of(new Choice(null, FAITHFUL_CHOICE), new Choice(null, BRIGHT_CHOICE),
                new Choice(null, METICULOUS_CHOICE));
    }

    public static NarrativeQuestion NARRATIVE_QUESTION() {
        return new NarrativeQuestion(null, INTRODUCTION_QUESTION, true, 1, 100);
    }

    public static SelectiveQuestion SELECTIVE_QUESTION() {
        return new SelectiveQuestion(null, CHOICES(), STRENGTH_QUESTION, true, 1, 1, 1);
    }
}
