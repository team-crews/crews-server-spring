package com.server.crews.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public NotFoundException(String attributeName, String target) {
        this(attributeName + "로 " + target + "을/를 찾을 수 없습니다.");
    }

    public NotFoundException(String message) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.message = message;
    }
}
