package com.conductor.core.model.event;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum EventFormat {
    IN_PERSON("in_person"),
    ONLINE("online"),
    HYBRID("hybrid");

    private String name;
    EventFormat(String name)
    {
        this.name = name;
    };

    public String getName(){
        return this.name;
    }

    public static String getAllOptions() {
        return Arrays.stream(EventFormat.values())
                .map(EventFormat::getName)
                .collect(Collectors.joining(", "));
    }
}
