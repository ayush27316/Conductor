package com.conductor.core.model.event;

import com.conductor.core.util.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum EventFormat{

    IN_PERSON("in_person"),

    ONLINE("online"),

    HYBRID("hybrid");

    private final String label;

    EventFormat(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static EventFormat fromValue(String value) {
        for (EventFormat format : values()) {
            if (format.label.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown event format: " + value);
    }

    /**
     * @return a comma-separated string of all event format options
     */
    public static String getAllOptions() {
        return Arrays.stream(EventFormat.values())
                .map(EventFormat::getLabel)
                .collect(Collectors.joining(", "));
    }
}
