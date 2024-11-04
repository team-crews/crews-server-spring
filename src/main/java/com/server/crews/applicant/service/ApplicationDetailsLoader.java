package com.server.crews.applicant.service;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.applicant.repository.NarrativeAnswerRepository;
import com.server.crews.applicant.repository.SelectiveAnswerRepository;
import com.server.crews.global.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationDetailsLoader {
    private final ApplicationRepository applicationRepository;
    private final SelectiveAnswerRepository selectiveAnswerRepository;
    private final NarrativeAnswerRepository narrativeAnswerRepository;

    public Application findByIdWithRecruitmentAndPublisher(Long applicationId) {
        return applicationRepository.findByIdWithRecruitmentAndPublisher(applicationId)
                .map(this::fetchAnswers)
                .orElseThrow(() -> new NotFoundException("지원서 id", "지원서"));
    }

    public Optional<Application> findNullableByApplicantIdAndRecruitmentCode(Long applicantId, String code) {
        return applicationRepository.findByApplicantIdAndRecruitmentCode(applicantId, code)
                .map(this::fetchAnswers);
    }

    private Application fetchAnswers(Application application) {
        List<NarrativeAnswer> narrativeAnswers = narrativeAnswerRepository.findAllByApplication(application);
        List<SelectiveAnswer> selectiveAnswers = selectiveAnswerRepository.findAllByApplication(application);
        application.replaceWithFetchedNarrativeAnswers(narrativeAnswers);
        application.replaceWithFetchedSelectiveAnswers(selectiveAnswers);
        return application;
    }
}
