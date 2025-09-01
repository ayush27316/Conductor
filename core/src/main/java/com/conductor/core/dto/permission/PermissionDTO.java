package com.conductor.core.dto.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PermissionDTO {

    @JsonProperty("resource_name")
    private String resourceName;
    @JsonProperty("resource_id")
    private String resourceId;
    //wrong name it should be privillege
    private Map<String,String> permissions;
    @JsonProperty("granted_at")
    private ZonedDateTime grantedAt;
    @JsonProperty("granted_by_user_id")
    private String grantedByUserExternalId;
    @JsonProperty("expires_at")
    private ZonedDateTime expiresAt;

}
