package com.conductor.core.model.common;

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

    public String getResourceName(){
        return this.name;
    }
}
