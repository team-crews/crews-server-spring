package com.server.crews.recruitment.mapper;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.dto.response.SectionResponse;
import java.util.List;

public class RecruitmentMapper {

    public static RecruitmentDetailsResponse recruitmentToRecruitmentDetailsResponse(Recruitment recruitment) {
        List<Section> sections = recruitment.getOrderedSections();
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
        return new Recruitment(
                recruitmentSaveRequest.id(),
                recruitmentSaveRequest.code(),
                recruitmentSaveRequest.title(),
                recruitmentSaveRequest.description(),
                recruitmentSaveRequest.deadline(),
                publisher,
                sections);
    }
}
