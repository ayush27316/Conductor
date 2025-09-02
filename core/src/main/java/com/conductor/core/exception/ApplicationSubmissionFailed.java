package com.conductor.core.exception;

import com.conductor.core.model.common.ResourceType;

public class ApplicationSubmissionFailed extends RuntimeException {

    ResourceType targetResourceType;

    public ApplicationSubmissionFailed(String message) {
        super(message);
    }

    public ApplicationSubmissionFailed(ResourceType targetResourceType, String message) {
        super(message);
    }

    public ApplicationSubmissionFailed() {
        super();
    }

    public ApplicationSubmissionFailed(ResourceType targetResourceType) {
        super();
        this.targetResourceType = targetResourceType;
    }

    public ApplicationSubmissionFailed(String message, Throwable cause) {
        super(message, cause);
    }
}
