package com.jasik.momsnaggingapi.infra.common.exception;

import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import lombok.Getter;

@Getter
public class ScheduleNotFoundException extends RuntimeException{

    private final ErrorCode errorCode;

    public ScheduleNotFoundException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
