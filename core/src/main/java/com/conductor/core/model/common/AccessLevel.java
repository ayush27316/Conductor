package com.conductor.core.model.common;

import com.conductor.core.util.Option;

/**
 * Defines the types of actions that can be granted
 * on a given {@link ResourceType}.
 */
public enum AccessLevel implements Option {

    /**
     * Grants read-only access to a resourceType.
     */
    READ("read"),

    /**
     * Grants permission to read, create,update or delete a resourceType.
     */
    WRITE("write");

    private String name;
    AccessLevel(String name){
        this.name = name;
    }

    @Override
    public String getName(){
        return this.name;
    }

}
