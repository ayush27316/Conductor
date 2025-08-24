package com.conductor.core.model.permission;

import java.util.Optional;

/**
 * Defines the types of actions that can be granted
 * on a given {@link Resource}.
 */
public enum AccessLevel {

    /**
     * Grants read-only access to a resource.
     */
    READ("read"),

    /**
     * Grants permission to read, create,update or delete a resource.
     */
    WRITE("write");

    private String name;
    AccessLevel(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public static Optional<AccessLevel> fromName(String name){
        AccessLevel level = null;
        if(name == "read"){
            level = AccessLevel.READ;
        } else if (name == "write") {
            level = AccessLevel.WRITE;
        }
        return Optional.ofNullable(level);
    }

}
