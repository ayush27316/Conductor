package com.conductor.core.model.event;

import com.conductor.core.model.permission.Resource;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines the different privileges that can be granted within an {@link Event}.
 * These privileges control access to event-specific resources and operations.
 * 
 * Note: Having organization-level EVENT_MANAGEMENT privilege grants the ability
 * to create events, but event-specific privileges control what can be done
 * within each individual event.
 */
public enum EventPrivilege {

    /**
     * Represents event operators that might facilitate check-ins, ticket approval etc.
     * <p>
     * Access to this resource controls which users can create, delete or update operators
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
     * <p>{@link EventAudit} can only be read. Therefore, AUDIT resource
     * can only have READ {@link com.conductor.core.model.permission.AccessLevel}
     * </p>
     */
    AUDIT("audit"),

    /**
     * Represents event members or participants of an event.
     * <p>
     * Access to this resource controls who can
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


    private String name;
    EventPrivilege(String name)
    {
        this.name = name;
    };

    public String getName(){
        return this.name;
    }



    private static final Map<String, EventPrivilege> LOOKUP =
            Stream.of(values()).collect(Collectors.toMap(EventPrivilege::getName, r -> r));

    /**
     * Resolves a resource from its string name.
     *
     * @param name the resource name
     * @return an Optional containing the matching Resource, or empty if not found
     */
    public static Optional<EventPrivilege> fromName(String name) {
        return Optional.ofNullable(LOOKUP.get(name));
    }
}
