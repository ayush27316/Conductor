package com.conductor.core.dto;

import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.model.common.Resource;
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

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Resource is required")
    private Resource resource;

    @NotNull(message = "Resource ID is required")
    private String resourceId;

    @NotNull(message = "Privilege is required")
    private String privilege;

    @NotNull(message = "Access level is required")
    private AccessLevel accessLevel;

    private ZonedDateTime expiresAt;
    private Map<String, String> metadata;
}
