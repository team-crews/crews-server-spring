package com.server.crews.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CrewsException extends RuntimeException {
    private final GeneralErrorCode errorCode;

    public CrewsException(GeneralErrorCode generalErrorCode) {
        super(generalErrorCode.getMessage());
        this.errorCode = generalErrorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    public int getCode() {
        return errorCode.getCode();
    }

    public String getMessage() {
        return errorCode.getMessage();
    }
}
