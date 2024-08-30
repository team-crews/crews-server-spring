package com.server.crews.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATE_SECRET_CODE(HttpStatus.BAD_REQUEST, "중복된 코드입니다."),
    NO_PARAMETER(HttpStatus.BAD_REQUEST, "%s 파라미터가 없습니다."),
    INVALID_EMAIL_PATTERN(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),
    INVALID_DEADLINE(HttpStatus.BAD_REQUEST, "모집 마감 기한은 현재 시각 이전이며 한 시간 단위입니다."),
    ALREADY_ANNOUNCED(HttpStatus.BAD_REQUEST, "모집 공고 결과 발표가 이미 완료되었습니다."),
    RECRUITMENT_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "모집이 이미 시작되었습니다."),
    RECRUITMENT_NOT_STARTED(HttpStatus.BAD_REQUEST, "모집이 시작되지 않았습니다."),

    NO_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰 형식이 잘못 되었습니다."),
    MALFORMED_JWT(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh token 입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 access token 입니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "권한이 없는 사용자입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않는 리프레시 토큰입니다."),

    RECRUITMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 모집 지원서 양식입니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 지원서입니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 문항입니다."),
    CHOICE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 선택지입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
