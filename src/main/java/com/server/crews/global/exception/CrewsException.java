package com.server.crews.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CrewsException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;
    private final Integer code;

    public CrewsException(GeneralErrorCode generalErrorCode) {
        this(generalErrorCode.getHttpStatus(), generalErrorCode.getMessage(), generalErrorCode.getCode());
    }

    public CrewsException(HttpStatus httpStatus, String message, Integer code) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
        this.code = code;
    }
}
