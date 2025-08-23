package com.conductor.core.dto;

import com.conductor.core.model.Event;
import com.conductor.core.model.EventAudit;
import com.conductor.core.model.Operator;
import com.conductor.core.model.Organization;
import com.conductor.core.model.ticket.Ticket;
import com.conductor.core.model.ticket.TicketCreationStrategy;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Event.Type type;
    private Event.Status status;
    private Event.Format format;
    private Event.AccessStrategy accessStrategy;
    private TicketCreationStrategy ticketCreationStrategy;
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
    private ZonedDateTime begin;
    private ZonedDateTime end;
}
