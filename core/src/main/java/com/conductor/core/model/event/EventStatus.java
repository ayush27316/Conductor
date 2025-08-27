package com.conductor.core.model.event;

import com.conductor.core.util.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents the lifecycle status of an {@link Event}.
 * <p>
 * An event can transition between these states based on its
 * creation, execution, and closure.
 * </p>
 */
public enum EventStatus {
    /**
     * The event has been created but not yet started/live.
     */
    DRAFT("draft"),

    /**
     * The event is currently live and accessible to participants.
     */
    LIVE("live"),

    /**
     * The event has ended and is no longer active.
     */
    EXPIRED("expired"),

    /**
     * The event has been cancelled before completion.
     */
    CANCELLED("cancelled");


    private final String label;

    EventStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static EventStatus fromValue(String value) {
        for (EventStatus status : values()) {
            if (status.label.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown event status: " + value);
    }

    /**
     * @return a comma-separated string of all event status options
     */
    public static String getAllOptions() {
        return Arrays.stream(EventStatus.values())
                .map(EventStatus::getLabel)
                .collect(Collectors.joining(", "));
    }
}
