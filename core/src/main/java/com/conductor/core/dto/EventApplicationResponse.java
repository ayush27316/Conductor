package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Response DTO for event application submissions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventApplicationResponse {
    
    @JsonProperty("application_id")
    private String applicationId;
    
    @JsonProperty("event_id")
    private String eventId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("submitted_at")
    private String submittedAt;
    
    @JsonProperty("message")
    private String message;
}
