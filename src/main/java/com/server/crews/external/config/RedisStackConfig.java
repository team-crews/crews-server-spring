package com.server.crews.external.config;

import com.redis.lettucemod.RedisModulesClient;
import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.spring.RedisModulesAutoConfiguration;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
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
    public RedisModulesClient redisSearchClient() {
        return RedisModulesClient.create("redis://" + host + ":" + port);
    }

    @Bean
    public GenericObjectPool<StatefulRedisModulesConnection<String, String>> redisStackConnectionPool() {

        GenericObjectPoolConfig<StatefulRedisModulesConnection<String, String>> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(4);
        config.setMinIdle(4);
        config.setJmxEnabled(false);

        return ConnectionPoolSupport.createGenericObjectPool(() -> redisSearchClient().connect(), config);
    }
}
