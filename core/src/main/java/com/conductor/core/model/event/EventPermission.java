package com.conductor.core.model.event;

import com.conductor.core.model.common.AccessLevel;
import jakarta.persistence.Embeddable;

import java.util.List;

public class EventPermission {

    private EventResource resources;
    private List<AccessLevel> accessLevels;

}
