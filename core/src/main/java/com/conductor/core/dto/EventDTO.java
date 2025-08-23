package com.conductor.core.dto;

import com.conductor.core.model.event.Event;
import com.conductor.core.model.ticket.TicketCreationStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Event.Status status;
    private Event.Format format;
    //private Event.AccessStrategy accessStrategy;
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
