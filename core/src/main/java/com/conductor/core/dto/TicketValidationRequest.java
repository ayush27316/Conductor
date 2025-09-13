package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationRequest {

    @NotBlank
    @JsonProperty("event-id")
    private String eventExternalId;

    @NotBlank
    @JsonProperty("ticket-id")
    private String ticketExternalId;
}
