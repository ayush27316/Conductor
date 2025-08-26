package com.conductor.core.model.event;

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
public enum EventOption {

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

    private final String name;

    EventOption(String name) {
        this.name = name;
    }

    /**
     * @return the string identifier of the option
     */
    public String getName() {
        return this.name;
    }

    private static final Map<String, EventOption> LOOKUP =
            Stream.of(values()).collect(Collectors.toMap(EventOption::getName, r -> r));

    /**
     * Resolves an EventOption from its string name.
     *
     * @param name the option name
     * @return an Optional containing the matching EventOption, or empty if not found
     */
    public static Optional<EventOption> fromName(String name) {
        return Optional.ofNullable(LOOKUP.get(name));
    }


    public static String getAllOptions() {
        return Arrays.stream(EventOption.values())
                .map(EventOption::getName)
                .collect(Collectors.joining(", "));
    }
}
