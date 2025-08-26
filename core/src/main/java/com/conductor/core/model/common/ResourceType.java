package com.conductor.core.model.common;

import com.conductor.core.util.Option;

/**
 * Defines all the resources in the system.
 */
public enum ResourceType implements Option {

    ORGANIZATION("organization"),
    EVENT("event"),
    USER("user"),
    TICKET("ticket");

    private String name;


    ResourceType(String name){
        this.name = name;
    }

    @Override
    public String getName(){
        return this.name;
    }

}

