package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.NarrativeAnswer;

public record NarrativeAnswerResponse(Long questionId, String answer) {

    public static NarrativeAnswerResponse from(NarrativeAnswer narrativeAnswer) {
        return new NarrativeAnswerResponse(narrativeAnswer.getNarrativeQuestion().getId(), narrativeAnswer.getContent());
    }
}
