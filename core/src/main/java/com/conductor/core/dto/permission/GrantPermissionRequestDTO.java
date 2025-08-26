package com.conductor.core.dto.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantPermissionRequestDTO {

    @NotNull(message = "Benefiting User's ID is required")
    @JsonProperty("benefiting-user-id")
    private String bUserExternalId;

    @NotNull(message = "ResourceType Name is required")
    @JsonProperty("resourceType-name")
    private String resourceName;

    @NotNull(message = "ResourceType ID is required")
    @JsonProperty("resourceType-id")
    private String resourceId;

    /*
    * Map permissions has Privilege's as keys
    * and AccessLevel as values.
    * */
    @NotNull(message = "Permissions are required")
    private Map<String,String> permissions;

    @JsonProperty("expires-at")
    private ZonedDateTime expiresAt;

}
