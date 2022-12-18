package com.jasik.momsnaggingapi.infra.common;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404,"USER-ERR-404","해당 유저를 찾을 수 없습니다."),
    SCHEDULE_NOT_FOUND(404,"SCHEDULE-ERR-404","해당 스케줄을 찾을 수 없습니다."),
    INTER_SERVER_ERROR(500,"COMMON-ERR-500","INTER SERVER ERROR"),
    EMAIL_DUPLICATION(400,"USER-ERR-400","EMAIL DUPLICATED"),
    THREAD_FULL(400,"COMMON-ERR-500","ASYNC THREAD FULLED"),
    ROUTINE_NOT_VALID(400,"SCHEDULE-ERR-400","반복 옵션이 올바르지 않습니다."),
    NOT_VALID_STATUS(400,"SCHEDULE-ERR-400","일정을 미루거나 연기할 수 없습니다."),
    FOLLOW_NOT_VALID(400,"FOLLOW-ERR-400","계정을 팔로우 할 수 없습니다."),
    FOLLOW_NOT_FOUND(404,"FOLLOW-ERR-404","계정 팔로우 정보가 없습니다."),
    REACTION_DUPLICATED(400,"REACTION-ERR-404","이미 추가한 반응입니다."),
    ;

    private final int status;
    private final String errorCode;
    private final String message;
}
