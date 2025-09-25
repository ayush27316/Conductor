package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterEventManagerRequest {

    @NotBlank(message = "Event id is required")
    @JsonProperty("event-id")
    String eventExternalId;

    //person who will be registered as a manager for the provided event
    @NotBlank(message = "User id is required")
    @JsonProperty("user-id")
    String userExternalId;
}
