package com.server.crews.applicant.dto.response;

import com.server.crews.applicant.domain.NarrativeAnswer;

public record NarrativeAnswerResponse(Long answerId, Long questionId, String content) {

    public static NarrativeAnswerResponse from(NarrativeAnswer narrativeAnswer) {
        return new NarrativeAnswerResponse(narrativeAnswer.getId(), narrativeAnswer.getNarrativeQuestion().getId(),
                narrativeAnswer.getContent());
    }
}
