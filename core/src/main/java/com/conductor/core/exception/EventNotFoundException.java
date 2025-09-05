package com.conductor.core.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }
    public EventNotFoundException(String message, Throwable t) {
        super(message, t);
    }

    public EventNotFoundException() {
        super();
    }
}