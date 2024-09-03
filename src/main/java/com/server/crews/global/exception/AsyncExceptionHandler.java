package com.server.crews.global.exception;

import com.server.crews.global.CustomLogger;
import java.lang.reflect.Method;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    private final CustomLogger customLogger = new CustomLogger(AsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable e, Method method, Object... params) {
        customLogger.error((Exception) e);
    }
}
