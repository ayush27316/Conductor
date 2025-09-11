package com.conductor.core.exception;

public class OperatorNotFoundException extends RuntimeException {
    public OperatorNotFoundException(Long userId) {
        super("Operator not found for user: " + userId);
    }

    public OperatorNotFoundException() {
        super();
    }
}
