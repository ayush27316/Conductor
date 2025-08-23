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
 * through {@link AccessStrategy}, and enforces time-based
 * constraints on accessibility.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAccessDetails {

    /**
     * Strategy that determines how tickets grant access to an event.
     * <ul>
     *   <li>{@code ONCE} - Ticket grants access exactly once.</li>
     *   <li>{@code LIMITED} - Ticket grants access a limited number of times.</li>
     *   <li>{@code UNLIMITED} - Ticket grants unlimited access.</li>
     * </ul>
     */
    public enum AccessStrategy {
        ONCE,
        LIMITED,
        UNLIMITED,

        /*No Implementation provided. The idea is that these Strategies
         * can be composed together under certain restrictions. */
        BLOCKED,
        TEMPORAL,
        FIRST_COME_FIRST_SERVED,
    }


    @Column(name = "access_strategy", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccessStrategy accessStrategy;

    @Column(name = "accessible_from", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime accessibleFrom;

    @Column(name = "accessible_to", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime accessibleTo;


}
