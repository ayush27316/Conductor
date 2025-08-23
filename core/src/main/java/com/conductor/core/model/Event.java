package com.conductor.core.model;

import com.conductor.core.model.listerners.DefaultEntityListener;
import com.conductor.core.model.ticket.Ticket;
import com.conductor.core.model.ticket.TicketCreationStrategy;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({DefaultEntityListener.class})
public class Event extends BaseEntity {

    public enum Type {
        MAIN, STEP, SUB_STEP
    }

    public enum Format {
        IN_PERSON, ONLINE, HYBRID
    }

    public enum Status {
        DRAFT, LIVE, EXPIRED, CANCELLED
    }

    public enum AccessStrategy {
        ONCE, LIMITED, UNLIMITED,
        TEMPORAL_ONCE, TEMPORAL_LIMITED, TEMPORAL_UNLIMITED
    }

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Format format;

    @Enumerated(EnumType.STRING)
    private AccessStrategy accessStrategy;

    @Embedded
    private EventAudit audit;

    @Enumerated(EnumType.STRING)
    private TicketCreationStrategy ticketCreationStrategy;

    @Column(unique = true, nullable = false)
    private String externalId;

    private String shortName;
    private String displayName;
    private String websiteUrl;
    private String externalUrl;
    private String termsAndConditionsUrl;
    private String privacyPolicyUrl;
    private String imageUrl;
    private String fileBlobId;
    private String location;
    private String latitude;
    private String longitude;

    @Column(name = "begin_time")
    private ZonedDateTime begin;

    @Column(name = "end_time")
    private ZonedDateTime end;

    @ManyToOne
    @JoinColumn(name = "organization_id_fk")
    @JsonBackReference
    private Organization organization;

    @ManyToMany
    @JsonManagedReference
    private Set<Operator> operators = new HashSet<>();

    @ManyToMany
    private List<Ticket> tickets;


    @PrePersist
    public void ensureExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
    }
}
