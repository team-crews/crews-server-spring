package com.server.crews.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDto {
    private Integer code;
    private String message;

    public ErrorDto(final CrewsException e) {
        this.code = e.getHttpStatus().value();
        this.message = e.getMessage();
    }
}