package com.conductor.core.exception;

public class OrganizationNotFound  extends RuntimeException {

    public OrganizationNotFound()
    {
        super();
    }

    public OrganizationNotFound(String message) {
        super(message);
    }
    public OrganizationNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}