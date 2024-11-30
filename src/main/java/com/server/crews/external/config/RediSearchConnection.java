package com.server.crews.external.config;

import com.redis.lettucemod.RedisModulesClient;
import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisModulesCommands;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class RediSearchConnection {
    private final StatefulRedisModulesConnection<String, String> redisModulesConnection;

    public RediSearchConnection(RedisModulesClient redisModulesClient) {
        this.redisModulesConnection = redisModulesClient.connect();
    }

    public RedisModulesCommands<String, String> getCommands() {
        return redisModulesConnection.sync();
    }

    @PreDestroy
    public void close() {
        redisModulesConnection.close();
    }
}
