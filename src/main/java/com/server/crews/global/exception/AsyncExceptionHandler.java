package com.server.crews.global.exception;

import com.server.crews.global.CustomLogger;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    private static final CustomLogger customLogger = new CustomLogger(AsyncExceptionHandler.class);

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void handleUncaughtException(Throwable e, Method method, Object... params) {
        customLogger.error((Exception) e);
        eventPublisher.publishEvent(new AsyncInternalErrorOccurredEvent((Exception) e));
    }
}
