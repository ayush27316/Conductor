package com.conductor.core.model.ticket;


import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/*
* A user creates a reservation to get a ticket for an event.
* Based on the event we keep track of documents, payments
* associated with this event.
* */
@Entity
@Table(name = "ticket_reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketReservation extends BaseEntity {

    public enum Status{
        PENDING,
        APPROVED
    }

    @Column(name = "external_id", unique = true, nullable = false, updatable = false, length = 36)
    @JsonProperty("id")
    private String externalId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id_fk")
    @JsonIgnore
    private Event event;

    @OneToOne
    @JoinColumn(name = "tickets")
    @JsonIgnore
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    //fields for documentataion that is needed for ticket's approval
    //for now we will go with json stores in metadata field
    @Column(columnDefinition = "TEXT")
    private String metadata;


    @PrePersist
    public void ensureExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
    }


}


