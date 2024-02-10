package com.server.crews.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {
    @ExceptionHandler(CrewsException.class)
    public ResponseEntity<ErrorDto> crewsExceptionHandler(CrewsException e){
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorDto(e));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        return ResponseEntity.status(ErrorCode.NO_PARAMETER.getHttpStatus())
                .body(new ErrorDto(ErrorCode.NO_PARAMETER.getHttpStatus().value(),
                        String.format(ErrorCode.NO_PARAMETER.getMessage(), e.getParameterName())));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorDto> handleException(Throwable e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }
}