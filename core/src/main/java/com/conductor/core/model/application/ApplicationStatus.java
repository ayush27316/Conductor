package com.conductor.core.model.application;

import com.conductor.core.model.event.Event;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines {@link Application} states.
 * These statuses indicate the lifecycle of an application
 * from submission to a final decision.
 */
public enum ApplicationStatus {

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

    /**
     * @return the string identifier of the reservation status
     */
    public String getName() {
        return this.name;
    }

    private static final Map<String, ApplicationStatus> LOOKUP =
            Stream.of(values()).collect(Collectors.toMap(ApplicationStatus::getName, r -> r));

    /**
     * Resolves an EventReservationStatus from its string name.
     *
     * @param name the status name
     * @return an Optional containing the matching EventReservationStatus, or empty if not found
     */
    public static Optional<ApplicationStatus> fromName(String name) {
        return Optional.ofNullable(LOOKUP.get(name));
    }

    /**
     * @return a comma-separated string of all reservation status options
     */
    public static String getAllOptions() {
        return Arrays.stream(ApplicationStatus.values())
                .map(ApplicationStatus::getName)
                .collect(Collectors.joining(", "));
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
}
