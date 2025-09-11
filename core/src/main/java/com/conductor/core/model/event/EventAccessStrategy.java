package com.conductor.core.model.event;

import com.conductor.core.model.Option;

/**
 * Defines the possible access control strategies for an {@link Event}.
 * <p>
 * These strategies determine how participants can access an event,
 * ranging from single-use access to more complex rules such as
 * temporal restrictions or first-come-first-served allocation.
 * </p>
 */
public enum EventAccessStrategy implements Option {

    /**
     * A participant is granted access only once.
     * After accessing, the user cannot re-enter.
     */
    ONCE("once"),

    /**
     * A participant has a limited number of accesses.
     * The limit may depend on the event configuration.
     */
    LIMITED("limited"),

    /**
     * A participant has unlimited access.
     * They can join or access the event any number of times.
     */
    UNLIMITED("unlimited"),

    /**
     * Access is blocked entirely.
     * This may represent a suspended or restricted state.
     */
    BLOCKED("blocked"),

    /**
     * Access is governed by time-based restrictions.
     * For example, access may be allowed only during a specific
     * time window or duration.
     */
    CUSTOM("temporal");

    private final String name;

    EventAccessStrategy(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
