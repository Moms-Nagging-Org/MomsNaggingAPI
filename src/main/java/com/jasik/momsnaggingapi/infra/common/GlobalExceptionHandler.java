package com.jasik.momsnaggingapi.infra.common;

import com.jasik.momsnaggingapi.infra.common.exception.NotValidRoutineException;
import com.jasik.momsnaggingapi.infra.common.exception.ScheduleNotFoundException;
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

    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleScheduleNotFoundException(ScheduleNotFoundException ex) {
        log.error("handleScheduleNotFoundException", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }
    @ExceptionHandler(NotValidRoutineException.class)
    public ResponseEntity<ErrorResponse> handleNotValidRoutineException(NotValidRoutineException ex) {
        log.error("handleNotValidRoutineException", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }
}


