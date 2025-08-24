package com.conductor.core.model.event;

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
}
