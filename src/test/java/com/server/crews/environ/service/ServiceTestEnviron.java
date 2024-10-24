package com.server.crews.environ.service;

import com.server.crews.applicant.domain.repository.ApplicationRepository;
import com.server.crews.applicant.domain.repository.NarrativeAnswerRepository;
import com.server.crews.applicant.domain.repository.SelectiveAnswerRepository;
import com.server.crews.auth.domain.repository.AdministratorRepository;
import com.server.crews.auth.domain.repository.ApplicantRepository;
import com.server.crews.recruitment.domain.repository.ChoiceRepository;
import com.server.crews.recruitment.domain.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.domain.repository.RecruitmentRepository;
import com.server.crews.recruitment.domain.repository.SectionRepository;
import com.server.crews.recruitment.domain.repository.SelectiveQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceTestEnviron {
    private final ApplicantRepository applicantRepository;
    private final AdministratorRepository administratorRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final SectionRepository sectionRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final ChoiceRepository choiceRepository;
    private final ApplicationRepository applicationRepository;
    private final NarrativeAnswerRepository narrativeAnswerRepository;
    private final SelectiveAnswerRepository selectiveAnswerRepository;
    private final PasswordEncoder passwordEncoder;

    public ApplicantRepository applicantRepository() {
        return applicantRepository;
    }

    public AdministratorRepository administratorRepository() {
        return administratorRepository;
    }

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

    public ApplicationRepository applicationRepository() {
        return applicationRepository;
    }

    public NarrativeAnswerRepository narrativeAnswerRepository() {
        return narrativeAnswerRepository;
    }

    public SelectiveAnswerRepository selectiveAnswerRepository() {
        return selectiveAnswerRepository;
    }

    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }
}
