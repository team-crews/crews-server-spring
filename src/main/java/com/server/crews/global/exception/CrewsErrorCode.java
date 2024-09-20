package com.server.crews.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CrewsErrorCode {
    INVALID_EMAIL_PATTERN(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다.", 1003),
    INVALID_DEADLINE(HttpStatus.BAD_REQUEST, "모집 마감 기한은 현재 시각 이전이며 한 시간 단위입니다.", 1004),
    INVALID_MODIFIED_DEADLINE(HttpStatus.BAD_REQUEST, "수정된 모집 마감 기한은 기존 기한 이후이며 모집 진행 중에만 수정할 수 있습니다.", 1005),
    INVALID_SELECTION_COUNT(HttpStatus.BAD_REQUEST, "선택형 문항의 최대 선택 개수는 최소 선택 개수보다 크거나 같습니다.", 1006),

    ALREADY_ANNOUNCED(HttpStatus.CONFLICT, "모집 공고 결과 발표가 이미 완료되었습니다.",1007),
    RECRUITMENT_ALREADY_STARTED(HttpStatus.CONFLICT, "모집이 이미 시작되었습니다.", 1008),
    RECRUITMENT_NOT_STARTED(HttpStatus.CONFLICT, "모집이 시작되지 않았습니다.", 1009),
    RECRUITMENT_CLOSED(HttpStatus.CONFLICT, "모집이 마감되었습니다.", 1010),

    NO_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다.", 1011),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰 형식이 잘못 되었습니다.", 1012),
    MALFORMED_JWT(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다.", 1013),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.", 1014),
    UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다.", 1015),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다.", 1016),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh token 입니다.", 1017),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 access token 입니다.", 1018),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다.", 1019),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "권한이 없는 사용자입니다.", 1020),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않는 리프레시 토큰입니다.", 1021),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.", 1022);

    private final HttpStatus httpStatus;
    private final String message;
    private final int code;
}
