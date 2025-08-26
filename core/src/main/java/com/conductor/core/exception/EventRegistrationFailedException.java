package com.conductor.core.exception;

public class EventRegistrationFailedException extends RuntimeException {
    public EventRegistrationFailedException(String message) {
        super(message);
    }

    public EventRegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}