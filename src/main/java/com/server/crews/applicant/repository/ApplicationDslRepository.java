package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Application;
import com.server.crews.recruitment.domain.Recruitment;

import java.util.List;

public interface ApplicationDslRepository {
    List<Application> findAllByRecruitmentId(Long id);
    List<Application> findAllWithApplicantByRecruitment(Recruitment recruitment);
}
