package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response format")
public  class Error {
    @Schema(description = "Error type",
            example = "Bad Request")
    @JsonProperty("error")
    private String error;

    private boolean success;

    @Schema(description = "Detailed error message",
            example = "This event does not accept applications")
    @JsonProperty("message")
    private String message;

    @Schema(description = "Timestamp of the error",
            example = "2024-01-15T10:30:00Z")
    @JsonProperty("timestamp")
    private String timestamp;

    @Schema(description = "Additional error details")
    @JsonProperty("details")
    private Map<String, Object> details;
}
