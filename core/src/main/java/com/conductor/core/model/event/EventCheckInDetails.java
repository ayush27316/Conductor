package com.conductor.core.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

@Embeddable
public class EventCheckInDetails {

    @Column(name="checkin_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventCheckInType type;


    private LocalDateTime checkInStart;
    private LocalDateTime checkInEnd;

}
