package com.server.crews.external.config;

import com.redis.lettucemod.RedisModulesClient;
import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.spring.RedisModulesAutoConfiguration;
import io.lettuce.core.RedisURI;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude = RedisModulesAutoConfiguration.class)
public class RedisStackConfig {
    private final String host;
    private final String port;

    public RedisStackConfig(@Value("${redis-stack.host}") String host, @Value("${redis-stack.port}") String port) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public RedisModulesClient redisModulesClient() {
        RedisURI redisURI = RedisURI.builder()
                .withHost(host)
                .withPort(Integer.parseInt(port))
                .withTimeout(Duration.ofSeconds(10))
                .build();
        return RedisModulesClient.create(redisURI);
    }

    @Bean
    public StatefulRedisModulesConnection<String, String> redisModulesConnection(
            RedisModulesClient redisModulesClient) {
        return redisModulesClient.connect();
    }
}
