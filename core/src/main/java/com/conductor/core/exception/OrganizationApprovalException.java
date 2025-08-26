package com.conductor.core.exception;

public class OrganizationApprovalException extends RuntimeException {

    public OrganizationApprovalException(String message) {
        super(message);
    }
    public OrganizationApprovalException(String message, Throwable cause) {
        super(message, cause);
    }
}
