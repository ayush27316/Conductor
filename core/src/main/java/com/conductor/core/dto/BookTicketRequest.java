package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO for booking tickets
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public  class BookTicketRequest {
    @NotBlank(message = "")
    @JsonProperty(namespace = "event_name")
    private String name;

}
