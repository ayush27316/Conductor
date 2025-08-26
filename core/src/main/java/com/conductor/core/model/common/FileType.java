package com.conductor.core.model.common;

import com.conductor.core.util.Option;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents the supported file types within the system.
 */
public enum FileType implements Option {

    PDF("pdf"),

    PNG("png");

    private final String name;

    FileType(String name) {
        this.name = name;
    }

    /**
     * Returns the lowercase name of the file type.
     */
    @Override
    public String getName() {
        return this.name;
    }

//    /**
//     * Returns a comma-separated list of all file type options.
//     */
//    public static String getAllOptions() {
//        return Arrays.stream(FileType.values())
//                .map(FileType::getName)
//                .collect(Collectors.joining(", "));
//    }
}
