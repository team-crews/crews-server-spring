package com.server.crews.recruitment.service;

import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_CODE;
import static com.server.crews.fixture.RecruitmentFixture.DEFAULT_DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;

import com.redis.lettucemod.RedisModulesClient;
import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisModulesCommands;
import com.server.crews.recruitment.domain.Recruitment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RecruitmentSearchServiceTest {

    @Autowired
    private RecruitmentSearchService recruitmentSearchService;

    @Autowired
    private RedisModulesClient redisSearchClient;

    private StatefulRedisModulesConnection<String, String> connect;
    private RedisModulesCommands<String, String> commands;

    @BeforeEach
    void setUp() {
        this.connect = redisSearchClient.connect();
        this.commands = connect.sync();
    }

    @AfterEach
    void tearDown() {
        this.connect.close();
    }

    @Test
    @DisplayName("모집 공고 검색에 대한 인덱스를 생성한다.")
    void createIndex() {
        // when
        recruitmentSearchService.createIndex();

        // then
        List<Object> recruitmentIndexInfo = commands.ftInfo("recruitment_idx");
        String allInfos = recruitmentIndexInfo.stream()
                .map(Object::toString)
                .collect(Collectors.joining());

        assertThat(allInfos).contains("HASH", "title", "deadline", "recruitment:");
    }

    @Test
    @DisplayName("모집 공고 제목을 텍스트 검색한다.")
    void saveAndSearch() {
        // given
        recruitmentSearchService.createIndex();

        recruitmentSearchService.saveRecruitment(
                new Recruitment(1l, DEFAULT_CODE, "멋쟁이사자처럼 서강대학교 99기 아기사자 모집", DEFAULT_DESCRIPTION,
                        LocalDateTime.of(2030, 10, 5, 0, 0, 0), null,
                        List.of()));
        recruitmentSearchService.saveRecruitment(
                new Recruitment(2l, DEFAULT_CODE, "대박 멋진 멋쟁이사자처럼 서강대학교 100기 아기사자 모집", DEFAULT_DESCRIPTION,
                        LocalDateTime.of(2030, 11, 5, 0, 0, 0), null,
                        List.of()));
        recruitmentSearchService.saveRecruitment(
                new Recruitment(3l, DEFAULT_CODE, "CEOS 백엔드 99기 모집", DEFAULT_DESCRIPTION,
                        LocalDateTime.of(2030, 11, 5, 0, 0, 0), null,
                        List.of()));

        // when
        List<String> results = recruitmentSearchService.findRecruitmentTitlesByKeyword("멋쟁이", 5);

        // then
        assertThat(results).hasSize(2)
                .contains("멋쟁이사자처럼 서강대학교 99기 아기사자 모집", "대박 멋진 멋쟁이사자처럼 서강대학교 100기 아기사자 모집");
    }
}
