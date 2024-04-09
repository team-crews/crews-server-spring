package com.server.crews.recruitment.application;

import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.*;
import com.server.crews.recruitment.dto.request.*;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final SectionRepository sectionRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;
    private final ChoiceRepository choiceRepository;

    @Transactional
    public void saveRecruitment(
            final Recruitment accessedRecruitment,
            final RecruitmentSaveRequest request) {
        accessedRecruitment.updateAll(request);
        recruitmentRepository.save(accessedRecruitment);

        List<Question> questions = new ArrayList<>();
        for (SectionRequest sectionRequest : request.getSections()) {
            Section section = sectionRepository.save(sectionRequest.toEntity(accessedRecruitment.getId()));
            questions.addAll(createQuestionsBySection(section.getId(), sectionRequest));
        }

        List<SelectiveQuestion> selectiveQuestions = saveSelectiveQuestions(questions);
        saveChoices(request, selectiveQuestions);
        saveNarrativeQuestions(questions);
    }

    private List<Question> createQuestionsBySection(final Long sectionId, final SectionRequest sectionRequest) {
        return sectionRequest.getQuestions()
                .stream()
                .map(questionRequest -> questionRequest.toEntity(sectionId))
                .toList();
    }

    private List<SelectiveQuestion> saveSelectiveQuestions(final List<Question> questions) {
        List<SelectiveQuestion> selectiveQuestions = questions.stream()
                .filter(question -> !question.isNarrative())
                .map(question -> (SelectiveQuestion) question)
                .toList();
        return selectiveQuestionRepository.saveAll(selectiveQuestions);
    }

    private void saveChoices(final RecruitmentSaveRequest request, final List<SelectiveQuestion> selectiveQuestions) {
        Map<Integer, List<String>> choicesByOrder = choicesBySelectiveQuestionOrder(request);
        Map<Integer, SelectiveQuestion> selectiveQuestionIdsByOrder = selectiveQuestionIdsByOrder(selectiveQuestions);
        List<Choice> choices = choicesByOrder.entrySet()
                .stream()
                .flatMap(choiceContentsByOrder -> choiceStreamByOrder(choiceContentsByOrder, selectiveQuestionIdsByOrder))
                .toList();
        choiceRepository.saveAll(choices);
    }

    private Map<Integer, List<String>> choicesBySelectiveQuestionOrder(final RecruitmentSaveRequest request) {
        return request.getSections().stream()
                .map(SectionRequest::getQuestions)
                .flatMap(List::stream)
                .filter(QuestionRequest::isSelective)
                .collect(toMap(QuestionRequest::getOrder, QuestionRequest::getChoices));
    }

    private Map<Integer, SelectiveQuestion> selectiveQuestionIdsByOrder(final List<SelectiveQuestion> selectiveQuestions) {
        return selectiveQuestions.stream()
                .collect(toMap(SelectiveQuestion::getOrder, identity()));
    }

    private Stream<Choice> choiceStreamByOrder(
            final Map.Entry<Integer, List<String>> choiceContentsByOrder,
            final Map<Integer, SelectiveQuestion> selectiveQuestionIdsByOrder) {
        return choiceContentsByOrder.getValue()
                .stream()
                .map(choiceContent -> new Choice(selectiveQuestionIdsByOrder.get(choiceContentsByOrder.getKey()), choiceContent));
    }

    private void saveNarrativeQuestions(final List<Question> questions) {
        List<NarrativeQuestion> narrativeQuestions = questions.stream()
                .filter(Question::isNarrative)
                .map(question -> (NarrativeQuestion) question)
                .toList();
        narrativeQuestionRepository.saveAll(narrativeQuestions);
    }

    public void updateProgressState(
            final Recruitment accessedRecruitment, final ProgressStateUpdateRequest request) {
        accessedRecruitment.updateProgress(request.progress());
        recruitmentRepository.save(accessedRecruitment);
    }

    public RecruitmentDetailsResponse getRecruitmentDetails(final Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        return RecruitmentDetailsResponse.from(recruitment);
    }

    public void updateDeadline(
            final Recruitment accessedRecruitment, final DeadlineUpdateRequest request) {
        accessedRecruitment.updateDeadline(request.deadline());
        recruitmentRepository.save(accessedRecruitment);
    }
}
