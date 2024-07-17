package com.server.crews.recruitment.application;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.ClosingDateUpdateRequest;
import com.server.crews.recruitment.dto.request.ProgressStateUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final AdministratorRepository administratorRepository;

    @Transactional
    public RecruitmentDetailsResponse createRecruitment(Long publisherId, RecruitmentSaveRequest request) {
        Administrator publisher = administratorRepository.findById(publisherId)
                .orElseThrow(() -> new CrewsException(ErrorCode.USER_NOT_FOUND));
        String code = UUID.randomUUID().toString();
        Recruitment recruitment = request.toRecruitment(code, publisher);
        recruitmentRepository.save(recruitment);
        return RecruitmentDetailsResponse.from(recruitment);
    }

    @Transactional
    public void updateProgressState(Long recruitmentId, ProgressStateUpdateRequest request) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        recruitment.updateProgress(request.progress());
    }

    public RecruitmentDetailsResponse findRecruitmentDetails(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsById(recruitmentId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        List<Section> sections = recruitment.getSections();
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllBySectionIn(sections);
        List<SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllWithChoicesInSections(sections);

        Map<Section, List<NarrativeQuestion>> narrativeQuestionsBySection = narrativeQuestions.stream()
                .collect(groupingBy(NarrativeQuestion::getSection));
        Map<Section, List<SelectiveQuestion>> selectiveQuestionsBySection = selectiveQuestions.stream()
                .collect(groupingBy(SelectiveQuestion::getSection));
        return RecruitmentDetailsResponse.from(recruitment, narrativeQuestionsBySection, selectiveQuestionsBySection);
    }

    @Transactional
    public void updateClosingDate(Long recruitmentId, ClosingDateUpdateRequest request) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        recruitment.updateClosingDate(request.closingDate());
    }
}
