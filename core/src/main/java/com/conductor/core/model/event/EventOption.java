package com.conductor.core.model.event;

import com.conductor.core.util.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines configurable options for {@link Event}.
 * These options control ticket distribution, approval flows,
 * and payment strategies within an event.
 */
public enum EventOption{

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
    REQUIRES_PAYMENT("requires_payment"),

    /**
     * Payment is required before booking a ticket.
     * Even if approval is also required, payment happens first.
     */
    REQUIRES_PAYMENT_PRE_BOOKING("requires_payment_pre_booking"),

    /**
     * Payment is required only after the booking has been approved.
     * Approval happens first, then payment is collected.
     */
    REQUIRES_PAYMENT_POST_APPROVAL("requires_payment_post_approval");

    private final String label;

    EventOption(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static EventOption fromValue(String value) {
        for (EventOption option : values()) {
            if (option.label.equalsIgnoreCase(value)) {
                return option;
            }
        }
        throw new IllegalArgumentException("Unknown event option: " + value);
    }

    /**
     * @return a comma-separated string of all event format options
     */
    public static String getAllOptions() {
        return Arrays.stream(EventOption.values())
                .map(EventOption::getLabel)
                .collect(Collectors.joining(", "));
    }
}
