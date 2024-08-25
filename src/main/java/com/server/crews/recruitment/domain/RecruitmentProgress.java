package com.server.crews.recruitment.domain;

public enum RecruitmentProgress {
    READY, // 모집 공고 작성 중
    IN_PROGRESS, // 모집 중
    COMPLETION, // 모집 완료 (평가 중)
    ANNOUNCED; // 평가 완료 (이메일 전송 완료)
}
