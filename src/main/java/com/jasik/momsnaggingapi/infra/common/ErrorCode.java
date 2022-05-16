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
    ;

    private final int status;
    private final String errorCode;
    private final String message;
}
