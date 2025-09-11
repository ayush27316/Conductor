package com.conductor.core.dto;

import com.conductor.core.model.event.Event;
import com.conductor.core.model.ticket.TicketStatus;
import com.conductor.core.model.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {

    private String code;

    @JsonProperty(namespace ="username")
    private String username;
    @JsonProperty("event_id")
    private String eventExternalId;
}
