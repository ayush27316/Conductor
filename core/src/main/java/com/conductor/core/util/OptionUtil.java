package com.conductor.core.util;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//instead of giving option throw exception
public final class OptionUtil {
    private OptionUtil() {}

    public static <E extends Enum<E> & Option> Optional<E> fromName(Class<E> enumClass, String name) {
        return Stream.of(enumClass.getEnumConstants())
                .filter(e -> e.getName().equals(name))
                .findFirst();
    }

    public static <E extends Enum<E> & Option> String getAllOptions(Class<E> enumClass) {
        return Stream.of(enumClass.getEnumConstants())
                .map(Option::getName)
                .collect(Collectors.joining(", "));
    }
}

