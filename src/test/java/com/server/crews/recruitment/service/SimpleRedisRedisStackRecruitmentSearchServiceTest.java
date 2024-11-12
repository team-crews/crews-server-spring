package com.server.crews.recruitment.service;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;

import com.server.crews.environ.repository.CacheStoreTest;
import com.server.crews.recruitment.domain.Recruitment;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SimpleRedisRedisStackRecruitmentSearchServiceTest extends CacheStoreTest {

    @Autowired
    private SimpleRedisRecruitmentSearchService simpleRedisRecruitmentSearchService;

    @Test
    @DisplayName("모집 공고 제목을 저장소에 저장하고 접두사로 찾는다.")
    void findRecruitmentTitlesByPrefix() {
        // given
        simpleRedisRecruitmentSearchService.saveRecruitment(
                new Recruitment(null, DEFAULT_CODE, "멋쟁이사자처럼 서강대학교 99기 아기사자 모집", DEFAULT_DESCRIPTION,
                        LocalDateTime.of(2030, 10, 5, 0, 0, 0), null,
                        List.of()));
        simpleRedisRecruitmentSearchService.saveRecruitment(
                new Recruitment(null, DEFAULT_CODE, "멋쟁이사자처럼 서강대학교 100기 아기사자 모집", DEFAULT_DESCRIPTION,
                        LocalDateTime.of(2030, 11, 5, 0, 0, 0), null,
                        List.of()));
        simpleRedisRecruitmentSearchService.saveRecruitment(
                new Recruitment(null, DEFAULT_CODE, "CEOS 백엔드 99기 모집", DEFAULT_DESCRIPTION,
                        LocalDateTime.of(2030, 11, 5, 0, 0, 0), null,
                        List.of()));

        // when
        List<String> results = simpleRedisRecruitmentSearchService.findRecruitmentTitlesByKeyword("멋쟁이", 2);

        // then
        assertThat(results).hasSize(2)
                .containsExactly("멋쟁이사자처럼 서강대학교 100기 아기사자 모집", "멋쟁이사자처럼 서강대학교 99기 아기사자 모집");
    }
}
