package com.conductor.core.model.application;

import com.conductor.core.model.Option;

/**
 * Defines {@link Application} states.
 * These statuses indicate the lifecycle of an application
 * from submission to a final decision.
 */
public enum ApplicationStatus implements Option {

    /**
     * Application has been submitted but not yet reviewed or confirmed.
     */
    PENDING("pending"),

    /**
     * Application request has been approved by the organizer.
     */
    APPROVED("approved"),

    /**
     * Application has been cancelled and is no longer valid.
     */
    REJECTED("rejected"),

    /**
     * Application was cancelled by the user.
     */
    CANCELLED("cancelled");

    private final String name;

    ApplicationStatus(String name) {
        this.name = name;
    }


    public boolean isFinalStatus() {
        return this == APPROVED || this == REJECTED || this == CANCELLED;
    }

    /**
     * Check if status allows processing
     */
    public boolean canBeProcessed() {
        return this == PENDING;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
