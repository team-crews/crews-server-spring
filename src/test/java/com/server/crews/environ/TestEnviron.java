package com.server.crews.environ;

import com.server.crews.applicant.repository.ApplicantRepository;
import com.server.crews.applicant.repository.NarrativeAnswerRepository;
import com.server.crews.applicant.repository.SelectiveAnswerRepository;
import com.server.crews.recruitment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestEnviron {
    private final RecruitmentRepository recruitmentRepository;
    private final SectionRepository sectionRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final ChoiceRepository choiceRepository;
    private final ApplicantRepository applicantRepository;
    private final NarrativeAnswerRepository narrativeAnswerRepository;
    private final SelectiveAnswerRepository selectiveAnswerRepository;

    public RecruitmentRepository recruitmentRepository() {
        return recruitmentRepository;
    }

    public SectionRepository sectionRepository() {
        return sectionRepository;
    }

    public NarrativeQuestionRepository narrativeQuestionRepository() {
        return narrativeQuestionRepository;
    }

    public SelectiveQuestionRepository selectiveQuestionRepository() {
        return selectiveQuestionRepository;
    }

    public ChoiceRepository choiceRepository() {
        return choiceRepository;
    }

    public ApplicantRepository applicantRepository() {
        return applicantRepository;
    }

    public NarrativeAnswerRepository narrativeAnswerRepository() {
        return narrativeAnswerRepository;
    }

    public SelectiveAnswerRepository selectiveAnswerRepository() {
        return selectiveAnswerRepository;
    }
}
