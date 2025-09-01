package com.conductor.core.model.ticket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents the lifecycle status of a {@link Ticket}.
 * <p>
 * Each status reflects the current state of the ticket in the
 * event lifecycle, including its availability, usage, and validity.
 * </p>
 */
public enum TicketStatus  {

    /**
     * The ticket has been created but not yet used.
     * It is in an idle state, awaiting action.
     */
    IDLE("idle"),

    /**
     * The ticket has been cancelled by the user or the system.
     * Cancelled tickets cannot be used for entry.
     */
    CANCELLED("cancelled"),

    /**
     * The ticket has been validated and used for event entry.
     * It is no longer available for reuse.
     */
    CHECKED_IN("checked_in"),

    /**
     * The ticket is no longer valid because the event has passed
     * or the validity window has expired.
     */
    EXPIRED("expired"),

    /**
     * The ticket has been explicitly revoked by the organizer.
     * Revoked tickets cannot be used, even if unused or valid otherwise.
     */
    REVOKED("revoked");

    private final String label;

    TicketStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static TicketStatus fromValue(String value) {
        for (TicketStatus status : values()) {
            if (status.label.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ticket status: " + value);
    }

    /**
     * @return a comma-separated string of all ticket status options
     */
    public static String getAllOptions() {
        return Arrays.stream(TicketStatus.values())
                .map(TicketStatus::getLabel)
                .collect(Collectors.joining(", "));
    }
}
