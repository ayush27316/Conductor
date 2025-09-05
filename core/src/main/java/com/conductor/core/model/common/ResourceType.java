package com.conductor.core.model.common;

/**
 * Defines all the resources in the system.
 */
public enum ResourceType implements Option {

    ORGANIZATION("organization"),
    EVENT("event"),
    USER("user"),
    OPERATOR("operator"),
    FORM("form"),
    APPLICATION("application"),
    TICKET("ticket");

    private final String name;

    ResourceType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
