package com.jasik.momsnaggingapi.infra.common;

import com.jasik.momsnaggingapi.infra.common.exception.DuplicateException;
import com.jasik.momsnaggingapi.infra.common.exception.NotValidException;
import com.jasik.momsnaggingapi.infra.common.exception.ThreadFullException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("handleException", ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.INTER_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ThreadFullException.class)
    public ResponseEntity<ErrorResponse> handleThreadFullException(ThreadFullException ex) {
        log.error("handleThreadFullException", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<ErrorResponse> handleNotValidException(NotValidException ex) {
        log.error("handleNotValidException", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(DuplicateException ex) {
        log.error("handleDuplicateException", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }
}


