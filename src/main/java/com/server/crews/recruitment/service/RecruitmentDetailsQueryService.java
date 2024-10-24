package com.server.crews.recruitment.service;

import com.server.crews.global.exception.NotFoundException;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruitmentDetailsQueryService {
    private final RecruitmentRepository recruitmentRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;

    public Recruitment findByCode(String code) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsByCode(code)
                .orElseThrow(() -> new NotFoundException("모집 공고 코드", "모집 공고"));
        return findAndReplaceQuestions(recruitment);
    }

    public Recruitment findByPublisher(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsByPublisherId(publisherId)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        return findAndReplaceQuestions(recruitment);
    }

    public Optional<Recruitment> findNullableByPublisher(Long publisherId) {
        return recruitmentRepository.findWithSectionsByPublisherId(publisherId)
                .map(this::findAndReplaceQuestions);
    }

    private Recruitment findAndReplaceQuestions(Recruitment recruitment) {
        Long recruitmentId = recruitment.getId();
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllByRecruitmentId(recruitmentId);
        List<SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllByRecruitmentId(recruitmentId);
        recruitment.replaceQuestions(narrativeQuestions, selectiveQuestions);
        return recruitment;
    }
}
