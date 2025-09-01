package com.conductor.core.model.ticket;

import com.conductor.core.model.common.Resource;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


/**
 * A ticket is a form of acceptance to an event or events.
 * Tickets don't have identity of the owner. Therefore, anyone
 * with a ticket can access events associated with it.
 */
@Entity
@Builder
@Data
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
public class Ticket extends Resource {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id_fk", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id_fk", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @PrePersist
    public void prePersist() {
        super.init(ResourceType.TICKET);
    }
}
