package com.conductor.core.exception;

public class ApplicationNotFound extends RuntimeException {
    public ApplicationNotFound(String message) {
        super(message);
    }
    public ApplicationNotFound() {
        super();
    }
    public ApplicationNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
