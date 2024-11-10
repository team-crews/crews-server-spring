package com.server.crews.recruitment.service;

import com.server.crews.global.exception.NotFoundException;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.repository.SectionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentDetailsLoader {
    private final RecruitmentRepository recruitmentRepository;
    private final SectionRepository sectionRepository;

    public Recruitment findWithSectionsByCode(String code) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsByCode(code)
                .orElseThrow(() -> new NotFoundException("모집 공고 코드", "모집 공고"));
        return fetchQuestions(recruitment);
    }

    public Recruitment findWithSectionsByTitle(String title) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsByTitle(title)
                .orElseThrow(() -> new NotFoundException("모집 공고 제목", "모집 공고"));
        return fetchQuestions(recruitment);
    }

    public Recruitment findWithSectionsByPublisherId(Long publisherId) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsByPublisherId(publisherId)
                .orElseThrow(() -> new NotFoundException("동아리 관리자 id", "모집 공고"));
        return fetchQuestions(recruitment);
    }

    public Optional<Recruitment> findNullableWithSectionsByPublisherId(Long publisherId) {
        return recruitmentRepository.findWithSectionsByPublisherId(publisherId)
                .map(this::fetchQuestions);
    }

    private Recruitment fetchQuestions(Recruitment recruitment) {
        Long recruitmentId = recruitment.getId();
        List<Section> sections = sectionRepository.findAllWithQuestionsByRecruitmentId(recruitmentId);
        recruitment.replaceSectionsWithFetchedData(sections);
        return recruitment;
    }
}
