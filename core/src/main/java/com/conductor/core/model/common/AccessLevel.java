package com.conductor.core.model.common;

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

}
