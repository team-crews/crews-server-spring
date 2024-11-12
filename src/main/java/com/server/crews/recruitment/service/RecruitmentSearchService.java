package com.server.crews.recruitment.service;

import com.server.crews.recruitment.domain.Recruitment;
import java.util.List;

public interface RecruitmentSearchService {
    void saveRecruitment(Recruitment recruitment);

    List<String> findRecruitmentTitlesByKeyword(String keyword, int limit);
}
