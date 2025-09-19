package com.conductor.core.dto.event;

import com.conductor.core.model.event.EventAccessStrategy;
import com.conductor.core.model.event.EventFormat;
import com.conductor.core.model.event.EventOption;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request DTO for registering an event")
public class EventModification {

//    @NotBlank(message = "Organization Id is required.")
//    //@Size(max = 36, message = "Organization Id must be a valid UUID (36 characters).")
//    @Schema(description = "Id of the organization under which this event will be registered")
//    @JsonProperty("organization_id")
//    private String organizationId;


//    @NotBlank(message = "Event name is required")
    @Size(max = 100, message = "Event name cannot exceed 100 characters")
    @Schema(description = "Name of the event")
    private String name;

    //@NotNull(message = "Event format is required")
    @Schema(implementation = EventFormat.class)
    private EventFormat format;

    //@NotBlank(message = "Location is required")
    @Schema(description = "Location of the event", example = "Montreal, Canada")
    private String location;

    //@NotNull(message = "Begin time is required")
    @JsonProperty("begin_time")
    @Schema(description = "Start time of the event", example = "2025-09-01T09:00:00Z")
    private LocalDateTime begin;

    //@NotNull(message = "End time is required")
    @JsonProperty("end_time")
    @Schema(description = "End time of the event", example = "2025-09-01T17:00:00Z")
    private LocalDateTime end;

    //@NotNull(message = "total_tickets_to_be_sold is required")
    @JsonProperty("total_tickets_to_be_sold")
    private int totalTicketsToBeSold;

    //issue here not taking multiple options
    @Schema(
            description = "Option available for event",
            implementation = EventOption.class
    )
    private List<EventOption> options;


    @JsonProperty("is_free")
    private boolean isFree;

    //need to provide a ticket price and currency only if isfree is false
    @JsonProperty("ticket_price")
    private BigDecimal ticketPrice;

    private String currency;

    @JsonProperty("access_strategy")
    @Schema(implementation = EventAccessStrategy.class)
    private EventAccessStrategy accessStrategy;

    private int accessibleLimitedNumberOftimes;

    //@NotNull(message = "Accessible from date is required")
    @JsonProperty("accessible_from")
    @Schema(description = "Date and time when event registration opens", example = "2025-08-01T00:00:00")
    private LocalDateTime accessibleFrom;

    //@NotNull(message = "Accessible to date is required")
    @JsonProperty("accessible_to")
    @Schema(description = "Date and time when event registration closes", example = "2025-08-25T23:59:59")
    private LocalDateTime accessibleTo;

    @JsonProperty("application_open")
    private LocalDateTime applicationsOpen;
    @JsonProperty("applications_close")
    private LocalDateTime applicationsClose;

    //@NotNull(message = "description is required")
    private String description;
}
