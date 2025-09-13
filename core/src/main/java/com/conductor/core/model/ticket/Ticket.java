package com.conductor.core.model.ticket;

import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @Column(name = "tags")
    private String tags; // comma-separated tags placed by organization

    @PrePersist
    public void prePersist() {
        super.init(ResourceType.TICKET, this);
    }

    /**
     * Creates a new Ticket object.
     * @param user user to which this ticket will be assigned must not transient or null.
     * @param event event to which this ticket can grant access. Must not be null or transient.
     * @return a new ticket
     */
    public static Ticket creatNewTicket(User user, Event event)
    {
        Ticket ticket = Ticket.builder()
                .user(user)
                .event(event)
                .status(TicketStatus.IDLE)
                .build();
        return ticket;
    }


}
