package com.conductor.core.model.event;

import com.conductor.core.model.permission.BaseEntity;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.ticket.TicketCreationStrategy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {

    /*
    * Resource based access requires globally unique id's.
    * This provides scope for application and database to
    * horizontally scale. [? arguable?]
    * */
    @Column(name="external_id", unique = true, nullable = false)
    private String externalId;

    private String status;
    private String format;

    @Column(name="short_name")
    private String shortName;

    @Column(name="display_name")
    private String displayName;

    @Column(name="website_url")
    private String websiteUrl;

    private String location;

    @Column(name = "begin_time")
    private ZonedDateTime begin;

    @Column(name = "end_time")
    private ZonedDateTime end;

    @Embedded
    private EventAccessDetails accessDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_creation_strategy")
    private TicketCreationStrategy ticketCreationStrategy;

    @ManyToOne
    @JoinColumn(name = "organization_id_fk")
    private Organization organization;

    /*
    * Maybe there is a better way to validate tickets for
    * an event instead of relaying on its association with
    * an event. Maybe we can generate a key for every event
    * and sign each ticket with this key. Then events key can
    * be cached and tickets can be validated easily.
    * @ManyToMany
    * private List<Ticket> tickets;
    **/
    @PrePersist
    public void ensureExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
    }
}
