package com.jasik.momsnaggingapi.infra.common;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    SCHEDULE_NOT_FOUND(404,"SCHEDULE-ERR-404","SCHEDULE NOT FOUND"),
    INTER_SERVER_ERROR(500,"COMMON-ERR-500","INTER SERVER ERROR"),
    EMAIL_DUPLICATION(400,"USER-ERR-400","EMAIL DUPLICATED"),
    THREAD_FULL(400,"COMMON-ERR-500","ASYNC THREAD FULLED"),
    ROUTINE_NOT_VALID(400,"SCHEDULE-ERR-400","WRONG REPEAT OPTION"),
    NOT_VALID_STATUS(400,"SCHEDULE-ERR-400","WRONG STATUS VALUE"),
    FOLLOW_NOT_VALID(400,"FOLLOW-ERR-400","계정을 팔로우 할 수 없습니다."),
    FOLLOW_NOT_FOUND(404,"FOLLOW-ERR-404","계정 팔로우 정보가 없습니다."),
    ;

    private final int status;
    private final String errorCode;
    private final String message;
}
