package com.conductor.core.exception;

public class ApplicationRequestFailedException  extends RuntimeException {
    public ApplicationRequestFailedException(String message) {
        super(message);
    }

    public ApplicationRequestFailedException(String message, Throwable t) {
        super(message, t);
    }

    public ApplicationRequestFailedException() {
        super();
    }
}