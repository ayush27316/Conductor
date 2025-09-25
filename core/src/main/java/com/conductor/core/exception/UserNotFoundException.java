package com.conductor.core.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException() {
        super();
    }
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

