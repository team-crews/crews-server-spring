package com.server.crews.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATE_SECRET_CODE(HttpStatus.BAD_REQUEST, "중복된 코드입니다."),
    NO_PARAMETER(HttpStatus.BAD_REQUEST, "%s 파라미터가 없습니다.");

    private HttpStatus httpStatus;
    private String message;
}
