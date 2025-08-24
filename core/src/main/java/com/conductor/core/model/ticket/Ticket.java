package com.conductor.core.model.ticket;

import com.conductor.core.model.permission.BaseEntity;
import com.conductor.core.model.event.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * A ticket is a form of acceptance to an event or events.
 * Tickets don't have identity of the owner. Therefore, anyone
 * with a ticket can access events associated with it.
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket extends BaseEntity {

    private String tier;

    private String codex;

//    @Enumerated(EnumType.STRING)
//    private TicketStatus status;


    /*arguable if we need a bidirectional mapping*/
    @OneToOne
    @JoinColumn(name = "ticket_reservations")
    private TicketReservation reservation;

    /**
     * This list only give specifics about events associated with this ticket
     * but whether events give access to this ticket depends on the event.
     * We should incorporate a key and token validation here instead of
     * relaying on associations with events.
     */
    @ManyToMany
    private List<Event> accessibleEvents;

}
