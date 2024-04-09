package com.server.crews.recruitment.application;

import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.SelectiveQuestion;

import java.util.List;

public class QuestionFixture {
    public static final String INTRODUCTION_QUESTION = "자기소개해주세요";
    public static final String STRENGTH_QUESTION = "장점을 골라주세요";
    public static final String FAITHFUL_CHOICE = "성실함";
    public static final String BRIGHT_CHOICE = "밝음";
    public static final String METICULOUS_CHOICE = "꼼꼼함";
    public static final List<String> STRENGTH_CHOICES = List.of(FAITHFUL_CHOICE, BRIGHT_CHOICE, METICULOUS_CHOICE);

    public static final List<Choice> CHOICES = List.of(new Choice(FAITHFUL_CHOICE), new Choice(BRIGHT_CHOICE), new Choice(METICULOUS_CHOICE));
    public static final NarrativeQuestion NARRATIVE_QUESTION = NarrativeQuestion.builder()
            .content(INTRODUCTION_QUESTION)
            .necessity(true)
            .order(1)
            .wordLimit(100)
            .build();
    public static final SelectiveQuestion SELECTIVE_QUESTION = SelectiveQuestion.builder()
            .content(STRENGTH_QUESTION)
            .necessity(true)
            .order(1)
            .minimumSelection(1)
            .maximumSelection(1)
            .choices(CHOICES)
            .build();
}
