package com.server.crews.recruitment.util;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecruitmentMapper {

    public static RecruitmentDetailsResponse recruitmentToRecruitmentDetailsResponse(Recruitment recruitment) {
        List<Section> sections = recruitment.getSections();
        List<SectionResponse> sectionResponses = sections.stream()
                .map(SectionMapper::sectionToSectionResponse)
                .toList();
        return RecruitmentDetailsResponse.builder()
                .id(recruitment.getId())
                .title(recruitment.getTitle())
                .description(recruitment.getDescription())
                .recruitmentProgress(recruitment.getProgress())
                .sections(sectionResponses)
                .deadline(recruitment.getDeadline())
                .code(recruitment.getCode())
                .build();
    }

    public static Recruitment recruitmentSaveRequestToRecruitment(RecruitmentSaveRequest recruitmentSaveRequest,
                                                                  Administrator publisher) {
        List<Section> sections = recruitmentSaveRequest.sections().stream()
                .map(SectionMapper::sectionSaveRequestToSection)
                .toList();
        LocalDateTime deadlineDateTime = LocalDateTime.parse(recruitmentSaveRequest.deadline(), DateTimeFormatter.ISO_DATE_TIME);
        return new Recruitment(
                recruitmentSaveRequest.id(),
                recruitmentSaveRequest.code(),
                recruitmentSaveRequest.title(),
                recruitmentSaveRequest.description(),
                deadlineDateTime,
                publisher,
                sections);
    }
}
