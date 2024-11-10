package com.server.crews.global;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class CustomLogger {
    private static final String PACKAGE_NAME = "com.server.crews";
    private final Logger logger;

    public CustomLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public void error(Exception e, HttpServletRequest request) {
        MDC.put("requestUri", request.getRequestURI());
        error(e);
    }

    public void error(Exception e) {
        MDC.put("exceptionName", e.getClass().getSimpleName());
        if (e.getStackTrace().length > 0) {
            MDC.put("className", e.getStackTrace()[0].getClassName());
        }
        MDC.put("exceptionMessage", e.getMessage());
        MDC.put("stackTrace", extractPackageStackTrace(e));
        logger.error("An error occurred");
        MDC.clear();
    }

    private String extractPackageStackTrace(Exception e) {
        StringBuilder stackTraceBuilder = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().startsWith(PACKAGE_NAME)) {
                stackTraceBuilder.append(element).append("\n");
            }
        }
        return stackTraceBuilder.toString();
    }

    public void info(String var1, Object... var2) {
        logger.info(var1, var2);
    }
}
