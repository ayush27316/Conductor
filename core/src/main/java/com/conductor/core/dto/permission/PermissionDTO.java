package com.conductor.core.dto.permission;

import com.conductor.core.model.permission.AccessLevel;
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


    @JsonProperty("user_id")
    private String userExternalId;
    @JsonProperty("resource_name")
    private String resourceName;
    @JsonProperty("resource_id")
    private String resourceId;
    private Map<String,String> permissions;

}
