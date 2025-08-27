package com.conductor.core.model.common;

import com.conductor.core.util.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Defines all the resources in the system.
 */
public enum ResourceType {

    ORGANIZATION("organization"),
    EVENT("event"),
    USER("user"),
    TICKET("ticket");

    private final String label;

    ResourceType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static ResourceType fromValue(String value) {
        for (ResourceType type : values()) {
            if (type.label.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown resource type: " + value);
    }

    /**
     * @return a comma-separated string of all resource type options
     */
    public static String getAllOptions() {
        return Arrays.stream(ResourceType.values())
                .map(ResourceType::getLabel)
                .collect(Collectors.joining(", "));
    }
}
