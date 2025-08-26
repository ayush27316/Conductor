package com.conductor.core.exception;

public class OrganizationRegistrationException extends RuntimeException {
    public OrganizationRegistrationException(String message) {
        super(message);
    }
    public OrganizationRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

