package com.conductor.core.model.event;

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
    LIVE("draft"),

    /**
     * The event has ended and is no longer active.
     */
    EXPIRED("expired"),

    /**
     * The event has been cancelled before completion.
     */
    CANCELLED("cancelled");


    private String name;
    EventStatus(String name)
    {
        this.name = name;
    };

    public String getName(){
        return this.name;
    }


    public static String getAllOptions() {
        return Arrays.stream(EventStatus.values())
                .map(EventStatus::getName)
                .collect(Collectors.joining(", "));
    }
}
