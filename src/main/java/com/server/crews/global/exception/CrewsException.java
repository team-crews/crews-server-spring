package com.server.crews.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CrewsException extends RuntimeException {
    private final CrewsErrorCode errorCode;

    public CrewsException(CrewsErrorCode crewsErrorCode) {
        super(crewsErrorCode.getMessage());
        this.errorCode = crewsErrorCode;
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
