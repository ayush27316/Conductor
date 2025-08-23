package com.conductor.core.model.event;

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
    DRAFT,

    /**
     * The event is currently live and accessible to participants.
     */
    LIVE,

    /**
     * The event has ended and is no longer active.
     */
    EXPIRED,

    /**
     * The event has been cancelled before completion.
     */
    CANCELLED
}
