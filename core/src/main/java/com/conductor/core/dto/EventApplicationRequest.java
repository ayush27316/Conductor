package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO for submitting event applications/reservations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventApplicationRequest {
    
    @NotBlank(message = "Event ID is required")
    @JsonProperty("event_id")
    private String eventExternalId;

}
