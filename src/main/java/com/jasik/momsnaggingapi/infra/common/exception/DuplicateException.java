package com.jasik.momsnaggingapi.infra.common.exception;

import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException{

    private final ErrorCode errorCode;

    public DuplicateException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
    public DuplicateException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
