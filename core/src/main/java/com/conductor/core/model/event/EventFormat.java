package com.conductor.core.model.event;

import com.conductor.core.model.common.Option;

public enum EventFormat implements Option {

    IN_PERSON("in_person"),

    ONLINE("online"),

    HYBRID("hybrid");

    private final String name;

    EventFormat(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
