package com.server.crews.recruitment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.server.crews.environ.repository.CacheStoreTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RecruitmentSearchCacheStoreTest extends CacheStoreTest {

    @Autowired
    private RecruitmentSearchCacheStore recruitmentSearchCacheStore;

    @Test
    @DisplayName("모집 공고 제목을 캐시에 저장하고 접두사로 찾는다.")
    void findRecruitmentTitlesByPrefix() {
        // given
        recruitmentSearchCacheStore.saveRecruitmentTitle("멋쟁이사자처럼 서강대학교 99기 아기사자 모집");
        recruitmentSearchCacheStore.saveRecruitmentTitle("멋쟁이사자처럼 서강대학교 100기 아기사자 모집");
        recruitmentSearchCacheStore.saveRecruitmentTitle("CEOS 백엔드 99기 모집");

        // when
        List<String> results = recruitmentSearchCacheStore.findRecruitmentTitlesByPrefix("멋쟁이", 2);

        // then
        assertThat(results).hasSize(2)
                .containsExactly("멋쟁이사자처럼 서강대학교 100기 아기사자 모집", "멋쟁이사자처럼 서강대학교 99기 아기사자 모집");
    }
}
