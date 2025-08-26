package com.conductor.core.exception;

public class TokenNotValidException  extends RuntimeException {
    public TokenNotValidException(String message) {
        super(message);
    }
}

