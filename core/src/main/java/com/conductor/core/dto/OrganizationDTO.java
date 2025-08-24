package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {
    @JsonProperty("organization_id")
    private String externalId;
    private String name;
    private String description;
    private String email;
    private List<String> tags;
    private String websiteUrl;
    private String locations;
}
