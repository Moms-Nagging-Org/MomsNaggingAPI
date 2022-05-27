package com.jasik.momsnaggingapi.infra.common.exception;

import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import lombok.Getter;

@Getter
public class NotValidRoutineException extends RuntimeException{

    private final ErrorCode errorCode;

    public NotValidRoutineException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
