package com.conductor.core.dto.permission;

import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for granting a single permission to a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantPermissionRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Resource is required")
    private Resource resource;

    @NotNull(message = "Privilege is required")
    private String privilege;

    @NotNull(message = "Access level is required")
    private AccessLevel accessLevel;

    private Long resourceId;

    private String resourceType;

    private String notes;
}
