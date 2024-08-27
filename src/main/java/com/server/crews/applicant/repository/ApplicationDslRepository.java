package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Application;
import java.util.List;

public interface ApplicationDslRepository {
    List<Application> findAllWithApplicantByPublisherId(Long publisherId);
}
