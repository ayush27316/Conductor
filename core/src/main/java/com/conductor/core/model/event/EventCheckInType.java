package com.conductor.core.model.event;

import com.conductor.core.model.Option;

public enum EventCheckInType implements Option {

    /**
     * attendies will be contacted for online checkin via email
     */
    ONLINE("online"),
    /**
     * Check in done via an operator
     */
    OFFLINE("offline"),
    HYBRID("hybrid");

    private String name;

    EventCheckInType(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
