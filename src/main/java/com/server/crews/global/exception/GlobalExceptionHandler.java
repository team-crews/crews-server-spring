package com.server.crews.global.exception;

import com.server.crews.global.CustomLogger;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    public static final int CONSTRAINT_VIOLATION_CODE = 2000;
    public static final int NOT_FOUND_CODE = 3000;

    private final CustomLogger customLogger = new CustomLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException e,
                                                                          HttpHeaders headers, HttpStatusCode status,
                                                                          WebRequest request) {
        String errorMessage = String.format("%s 파라미터가 없습니다.", e.getParameterName());
        return ResponseEntity.badRequest().body(new ErrorResponse(errorMessage, null));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        String errorMessage = Objects.requireNonNull(e.getBindingResult().getFieldError())
                .getDefaultMessage();
        return ResponseEntity.badRequest().body(new ErrorResponse(errorMessage, null));
    }

    @ExceptionHandler(CrewsException.class)
    public ResponseEntity<ErrorResponse> handelNotFoundException(CrewsException e) {
        customLogger.error(e);
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getMessage(), e.getCode()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handelNotFoundException(NotFoundException e) {
        customLogger.error(e);
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getMessage(), NOT_FOUND_CODE));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        customLogger.error(e);
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage, CONSTRAINT_VIOLATION_CODE));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception e) {
        customLogger.error(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
