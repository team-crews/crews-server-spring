package com.server.crews.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CrewsException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public CrewsException(ErrorCode errorCode) {
        this(errorCode.getHttpStatus(), errorCode.getMessage());
    }

    public CrewsException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
