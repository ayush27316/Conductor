package com.conductor.core.dto;

import com.conductor.core.model.event.EventAccessStrategy;
import com.conductor.core.model.event.EventFormat;
import com.conductor.core.model.event.EventOption;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;


@Data
@Builder
@Schema(description = "Request DTO for registering an event")
public class EventDTO {

    private String id;

    private String organizationId;

    @NotBlank(message = "Event name is required")
    @Size(max = 100, message = "Event name cannot exceed 100 characters")
    @Schema(description = "Name of the event", example = "Tech Conference 2025")
    private String name;

   @Schema(implementation = EventFormat.class)
   private EventFormat format;

    @NotBlank(message = "Location is required")
    @Schema(description = "Location of the event", example = "Montreal, Canada")
    private String location;

    @NotNull(message = "Begin time is required")
    @JsonProperty("begin_time")
    @Schema(description = "Start time of the event", example = "2025-09-01T09:00:00Z")
    private LocalDateTime begin;

    @NotNull(message = "End time is required")
    @JsonProperty("end_time")
    @Schema(description = "End time of the event", example = "2025-09-01T17:00:00Z")
    private LocalDateTime end;

    @NotNull(message = "total_tickets_to_be_sold is required")
    @JsonProperty("total_tickets_to_be_sold")
    private int totalTicketsToBeSold;

    @Schema(
            description = "Option available for event",
            implementation = EventOption.class
    )
    //@EnumValue(enumClass = EventOption.class)
    private List<EventOption> options;

    @JsonProperty("access_strategy")
    @Schema(implementation = EventAccessStrategy.class)
    //@EnumValue(enumClass=EventAccessStrategy.class)
    private EventAccessStrategy accessStrategy;

    @NotNull(message = "Accessible from date is required")
    @JsonProperty("accessible_from")
    @Schema(description = "Date and time when event registration opens", example = "2025-08-01T00:00:00")
    private LocalDateTime accessibleFrom;

    @NotNull(message = "Accessible to date is required")
    @JsonProperty("accessible_to")
    @Schema(description = "Date and time when event registration closes", example = "2025-08-25T23:59:59")
    private LocalDateTime accessibleTo;

    @NotNull(message = "description is required")
    private String description;

}
