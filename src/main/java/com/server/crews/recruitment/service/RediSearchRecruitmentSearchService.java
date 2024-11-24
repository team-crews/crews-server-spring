package com.server.crews.recruitment.service;

import com.redis.lettucemod.api.sync.RedisModulesCommands;
import com.redis.lettucemod.search.CreateOptions;
import com.redis.lettucemod.search.Document;
import com.redis.lettucemod.search.Field;
import com.redis.lettucemod.search.Limit;
import com.redis.lettucemod.search.SearchOptions;
import com.redis.lettucemod.search.SearchResults;
import com.server.crews.external.config.RediSearchConnection;
import com.server.crews.recruitment.domain.Recruitment;
import io.lettuce.core.RedisCommandExecutionException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RediSearchRecruitmentSearchService implements RecruitmentSearchService {
    private static final String INDEX_KEY = "recruitment:";
    private static final String INDEX_NAME = "recruitment_idx";
    private static final ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");

    private final RediSearchConnection rediSearchConnection;

    @Override
    public void saveRecruitment(Recruitment recruitment) {
        String recruitmentKey = INDEX_KEY + recruitment.getId();
        RedisModulesCommands<String, String> commands = rediSearchConnection.getCommands();
        commands.hset(recruitmentKey, "title", recruitment.getTitle());
        commands.hset(recruitmentKey, "deadline", String.valueOf(getUnixTimestamp(recruitment.getDeadline())));
    }

    private static long getUnixTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(seoulZoneId).toEpochSecond();
    }

    @Override
    public List<String> findRecruitmentTitlesByKeyword(String keyword, int limit) {
        RedisModulesCommands<String, String> commands = rediSearchConnection.getCommands();

        SearchOptions searchOptions = new SearchOptions();
        searchOptions.setLimit(new Limit(0, limit));
        String query = "*" + keyword + "*";

        SearchResults searchResults = commands.ftSearch(INDEX_NAME, query, searchOptions);

        List<Document<String, String>> documents = searchResults.stream().toList();
        return documents.stream()
                .map(document -> document.get("title"))
                .toList();

    }

    /*
    운영 서버에서는 사용하지 말 것.
     */
    public void createIndex() {
        RedisModulesCommands<String, String> commands = rediSearchConnection.getCommands();
        if (!isIndexExists(commands)) {
            CreateOptions createOptions = new CreateOptions.Builder().prefix(INDEX_KEY).build();
            commands.ftCreate(INDEX_NAME, createOptions,
                    Field.text("title").build(),
                    Field.numeric("deadline").build());
        }
    }

    private boolean isIndexExists(RedisModulesCommands<String, String> commands) {
        try {
            commands.ftInfo(INDEX_NAME);
            return true;
        } catch (RedisCommandExecutionException e) {
            return false;
        }
    }
}
