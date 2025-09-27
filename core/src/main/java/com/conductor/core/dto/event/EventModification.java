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
public class EventModification {

//    @NotBlank(message = "Organization Id is required.")
//    //@Size(max = 36, message = "Organization Id must be a valid UUID (36 characters).")
//    @Schema(description = "Id of the organization under which this event will be registered")
//    @JsonProperty("organization_id")
//    private String organizationId;


//    @NotBlank(message = "Event name is required")
    @Size(max = 100, message = "Event name cannot exceed 100 characters")
    private String name;

    //@NotNull(message = "Event format is required")
    private EventFormat format;

    //@NotBlank(message = "Location is required")
    private String location;

    //@NotNull(message = "Begin time is required")
    @JsonProperty("begin_time")
    private LocalDateTime begin;

    //@NotNull(message = "End time is required")
    @JsonProperty("end_time")
    private LocalDateTime end;

    //@NotNull(message = "total_tickets_to_be_sold is required")
    @JsonProperty("total_tickets_to_be_sold")
    private Integer totalTicketsToBeSold;

    //issue here not taking multiple options
    private List<EventOption> options;


    @JsonProperty("is_free")
    private Boolean isFree;

    //need to provide a ticket price and currency only if isfree is false
    @JsonProperty("ticket_price")
    private BigDecimal ticketPrice;

    private String currency;

    @JsonProperty("access_strategy")
    private EventAccessStrategy accessStrategy;

    private Integer accessibleLimitedNumberOftimes;

    //@NotNull(message = "Accessible from date is required")
    @JsonProperty("accessible_from")
    private LocalDateTime accessibleFrom;

    //@NotNull(message = "Accessible to date is required")
    @JsonProperty("accessible_to")
    private LocalDateTime accessibleTo;

    @JsonProperty("application_open")
    private LocalDateTime applicationsOpen;

    @JsonProperty("applications_close")
    private LocalDateTime applicationsClose;

    //@NotNull(message = "description is required")
    private String description;
}
