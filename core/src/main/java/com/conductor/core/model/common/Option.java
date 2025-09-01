package com.conductor.core.model.common;

import com.conductor.core.model.permission.Privilege;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A contract for all constants defined as Enum for type safety in the data model.
 * Static methods defined in this contract: {@code fromName} and {@code getAllOptions};
 * allows for better interaction with the data model with services.
 *
 */
public interface Option {

    /**
     * @return the display name for this option
     */
    String getName();

    /**
     * Looks up an Option by its display name using case-insensitive matching.
     *
     * @param <E> the enum type that implements Option
     * @param enumClass the Class object representing the enum type
     * @param name the display name to search for (case-insensitive, may be null)
     * @return an Optional containing the matching enum constant, or empty if no match found
     *
     * @throws IllegalArgumentException if enumClass is null
     *
     */

    static <E extends Enum<E> & Option> Optional<E> fromName(Class<E> enumClass, String name) {

        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * @param <E> the enum type that implements Option
     * @param enumClass the Class object representing the enum type
     * @return a comma-separated string of all options
     */
    static <E extends Enum<E> & Option> String getAllOptions(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Option::getName)
                .collect(Collectors.joining(", "));
    }
}
