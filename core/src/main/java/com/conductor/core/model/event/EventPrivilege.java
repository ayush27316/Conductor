package com.conductor.core.model.event;

import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.util.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Defines the different privileges that can be granted within an {@link Event}.
 * These privileges control access to event-specific resources and operations.
 * 
 * Note: Having organization-level EVENT_MANAGEMENT privilege grants the ability
 * to create events, but event-specific privileges control what can be done
 * within each individual event.
 */
public enum EventPrivilege  {

    /**
     * Represents event operators that might facilitate check-ins, ticket approval etc.
     * <p>
     * Access to this resourceType controls which users can create, delete or update operators
     * for this specific event.
     * </p>
     */
    OPERATOR("operator"),

    /**
     * Represents event configuration settings like name, description, dates, location.
     * Users with this privilege can modify event details.
     */
    CONFIG("config"),

    /**
     * Represents audit-related information for this event.
     * <p>{@link EventAudit} can only be read. Therefore, AUDIT resourceType
     * can only have READ {@link AccessLevel}
     * </p>
     */
    AUDIT("audit"),

    /**
     * Represents event members or participants of an event.
     * <p>
     * Access to this resourceType controls who can
     * view, add, remove, or manage members within
     * the context of this specific event.
     * </p>
     */
    MEMBER("member"),

    /**
     * Represents ticket management for this event.
     * Users with this privilege can create, modify, and manage tickets.
     */
    TICKET("ticket"),

    /**
     * Represents the ability to view event details and basic information.
     * This is typically the minimum privilege needed to access an event.
     */
    VIEW("view");


    private final String label;

    EventPrivilege(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static EventPrivilege fromValue(String value) {
        for (EventPrivilege privilege : values()) {
            if (privilege.label.equalsIgnoreCase(value)) {
                return privilege;
            }
        }
        throw new IllegalArgumentException("Unknown event privilege: " + value);
    }

    /**
     * @return a comma-separated string of all event privilege options
     */
    public static String getAllOptions() {
        return Arrays.stream(EventPrivilege.values())
                .map(EventPrivilege::getLabel)
                .collect(Collectors.joining(", "));
    }
}
