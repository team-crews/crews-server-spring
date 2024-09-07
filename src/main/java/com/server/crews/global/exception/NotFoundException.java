package com.server.crews.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends CrewsException {

    public NotFoundException(String attributeName, String target) {
        this(attributeName + "로 " + target + "을/를 찾을 수 없습니다.");
    }

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
