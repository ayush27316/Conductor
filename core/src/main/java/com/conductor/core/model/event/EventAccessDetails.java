package com.conductor.core.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Represents access-related details for an {@link Event}.
 *
 * Defines how and when tickets grant access to an event
 * through {@link EventAccessStrategy}, and enforces time-based
 * constraints on accessibility.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAccessDetails {


    @Column(name = "access_strategy", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventAccessStrategy accessStrategy;

    @Column(name = "accessible_from", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime accessibleFrom;

    @Column(name = "accessible_to", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime accessibleTo;

}
