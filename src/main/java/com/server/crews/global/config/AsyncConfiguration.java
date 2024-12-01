package com.server.crews.global.config;

import com.server.crews.global.exception.AsyncExceptionHandler;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfiguration implements AsyncConfigurer {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler(applicationEventPublisher);
    }

    @Bean(name = "emailThreadPoolTaskExecutor")
    public Executor emailThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(6);
        threadPoolTaskExecutor.setMaxPoolSize(6);
        threadPoolTaskExecutor.setQueueCapacity(300);
        threadPoolTaskExecutor.setThreadNamePrefix("Async-Email-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
