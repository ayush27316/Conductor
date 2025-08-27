package com.conductor.core.model.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Defines supported file types.
 */
public enum FileType {

    PDF("pdf"),
    PNG("png");

    private final String label;

    FileType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static FileType fromValue(String value) {
        for (FileType type : values()) {
            if (type.label.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown file type: " + value);
    }

    /**
     * @return a comma-separated string of all file type options
     */
    public static String getAllOptions() {
        return Arrays.stream(FileType.values())
                .map(FileType::getLabel)
                .collect(Collectors.joining(", "));
    }
}
