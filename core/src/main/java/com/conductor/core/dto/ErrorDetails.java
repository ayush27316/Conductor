package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Standard error response")
public class ErrorDetails {

    @JsonProperty("error_code")
    private int code;
    @JsonProperty("error_description")
    private String description;

}