package com.conductor.core.dto.permission;

import com.conductor.core.model.permission.Resource;
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
public class RevokePermissionRequestDTO {


    @NotNull(message = "Target User's ID is required")
    @JsonProperty("target-user-id")
    private String targetUserExternalId;

    @NotNull(message = " Providing User's Id is required")
    @JsonProperty("providing-user-id")
    private Long pUserExternalId;

    @NotNull(message = "Resource Name is required")
    @JsonProperty("resource-name")
    private String resourceName;

    @NotNull(message = "Resource ID is required")
    @JsonProperty("resource-id")
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
