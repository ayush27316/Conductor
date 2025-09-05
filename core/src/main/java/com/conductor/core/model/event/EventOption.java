package com.conductor.core.model.event;

import com.conductor.core.model.common.Option;

/**
 * Defines configurable options for {@link Event}.
 * These options control ticket distribution, approval flows,
 * and payment strategies within an event.
 */
public enum EventOption implements Option {

    /**
     * Tickets can only be distributed before the event starts.
     * Once the event begins, no further ticket distribution is allowed.
     */
    TICKET_DISTRIBUTION_PRE_EVENT_ONLY("ticket_distribution_pre_event_only"),

    /**
     * Tickets can be distributed throughout the event duration.
     * Users may acquire tickets before or even during the event.
     */
    TICKET_DISTRIBUTION_THROUGHOUT_EVENT("ticket_distribution_throughout_event"),

    /**
     * Attendance or participation requires organizer approval.
     * Users must submit a request that is explicitly approved.
     */
    REQUIRES_APPROVAL("requires_approval"),

    /**
     * Participation requires payment.
     * May be combined with approval or distribution strategies.
     */
    REQUIRES_PAYMENT("requires_payment");

    private final String name;

    EventOption(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
