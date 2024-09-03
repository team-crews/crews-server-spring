package com.server.crews.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class ErrorLogger {

    public static void log(Throwable e) {
        MDC.put("lineNumber", String.valueOf(e.getStackTrace()[0].getLineNumber()));
        MDC.put("className", e.getStackTrace()[0].getClassName());
        MDC.put("exceptionName", e.getClass().getSimpleName());
        MDC.put("exceptionMessage", e.getMessage());
        log.error("An error occurred");
        MDC.clear();
    }
}
