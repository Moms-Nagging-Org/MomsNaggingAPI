package com.jasik.momsnaggingapi.infra.common.exception;

import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{

    private final ErrorCode errorCode;

    public NotFoundException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
    public NotFoundException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
