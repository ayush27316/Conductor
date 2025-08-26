package com.conductor.core.exception;

import jakarta.persistence.criteria.CriteriaBuilder;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidEmailException standard(){
        return new InvalidEmailException("Email not valid");
    }
}