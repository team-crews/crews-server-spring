package com.server.crews.global.exception;

public class CrewsException extends RuntimeException{
    private final ErrorCode errorCode;

    public CrewsException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
