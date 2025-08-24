package com.conductor.core.model.permission;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines all the resources in the system that can have permissions
 * applied to them.
 */
public enum Resource {

    ORGANIZATION("organization"),
    EVENT("event"),
    USER("user"),
    TICKET("ticket");

    private String name;


    Resource(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }


    private static final Map<String, Resource> LOOKUP =
            Stream.of(values()).collect(Collectors.toMap(Resource::getName, r -> r));

    /**
     * Resolves a resource from its string name.
     *
     * @param name the resource name
     * @return an Optional containing the matching Resource, or empty if not found
     */
    public static Optional<Resource> fromName(String name) {
        return Optional.ofNullable(LOOKUP.get(name));
    }
}

