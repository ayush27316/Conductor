package com.conductor.core.exception;

public class InvalidFormSubmissionException extends RuntimeException {
    public InvalidFormSubmissionException(String message) {
        super(message);
    }
}
