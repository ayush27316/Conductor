package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationModificationRequest {
    private String description;
    private String email;
    private List<String> tags;
    @JsonProperty("website-url")
    private String websiteUrl;
}
