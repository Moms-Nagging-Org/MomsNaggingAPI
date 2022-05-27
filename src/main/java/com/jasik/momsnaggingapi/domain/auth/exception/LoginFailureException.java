package com.jasik.momsnaggingapi.domain.auth.exception;

public class LoginFailureException extends RuntimeException {

    public LoginFailureException() {
        super("Failed to login.");
    }

    public LoginFailureException(String message) {
        super(message);
    }
}