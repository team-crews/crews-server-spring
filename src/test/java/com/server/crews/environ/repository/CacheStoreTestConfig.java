package com.server.crews.environ.repository;

import com.server.crews.recruitment.repository.RecruitmentSearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
@TestConfiguration
public class CacheStoreTestConfig {

    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public RecruitmentSearchKeywordRepository recruitmentSearchCacheStore() {
        return new RecruitmentSearchKeywordRepository(redisTemplate);
    }
}
