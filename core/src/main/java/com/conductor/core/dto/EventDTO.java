package com.conductor.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private String externalId;
    private String status;
    private String format;
    private String accessStrategy;
    private String ticketCreationStrategy;
    private LocalDateTime accessibleFrom;
    private LocalDateTime accessibleTo;
    private String shortName;
    private String displayName;
    private String websiteUrl;
    private String externalUrl;
    private String location;
    private String latitude;
    private String longitude;
    private ZonedDateTime begin;
    private ZonedDateTime end;
    private String organizationExternalId;

}
