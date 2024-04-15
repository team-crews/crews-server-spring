package com.server.crews.applicant.dto.request;

import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.recruitment.dto.request.QuestionType;

import java.util.List;

public record AnswerSaveRequest(QuestionType questionType, Long questionId, String content, List<Long> choiceIds) {
    public boolean isSelective() {
        return questionType == QuestionType.SELECTIVE;
    }

    public boolean isNarrative() {
        return questionType == QuestionType.NARRATIVE;
    }

    public List<SelectiveAnswer> toSelectiveAnswers(final Long applicantId) {
        return choiceIds.stream()
                .map(choiceId -> toSelectiveAnswer(applicantId, choiceId))
                .toList();
    }

    private SelectiveAnswer toSelectiveAnswer(final Long applicantId, final Long choiceId) {
        return SelectiveAnswer.builder()
                .selectiveQuestionId(questionId)
                .choiceId(choiceId)
                .applicantId(applicantId)
                .build();
    }

    public NarrativeAnswer toNarrativeAnswer(final Long applicantId) {
        return NarrativeAnswer.builder()
                .narrativeQuestionId(questionId)
                .content(content)
                .applicantId(applicantId)
                .build();
    }
}
