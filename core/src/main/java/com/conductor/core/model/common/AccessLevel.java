package com.conductor.core.model.common;

import com.conductor.core.model.event.EventStatus;
import com.conductor.core.util.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines the types of actions that can be granted
 * on a given {@link ResourceType}.
 */
public enum AccessLevel {

    /**
     * Grants read-only access to a resourceType.
     */
    READ("read"),

    /**
     * Grants permission to read, create,update or delete a resourceType.
     */
    WRITE("write");


    private String label;

    AccessLevel(String label)
    {
        this.label=label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static AccessLevel fromValue(String value) {
        for (AccessLevel level : values()) {
            if (level.label.equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown level: " + value);
    }

}
