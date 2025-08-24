package com.conductor.core.dto.permission;

import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * DTO for permission responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDTO {

    private Long id;
    private Long userId;
    private String username;
    private Resource resource;
    private String privilege;
    private AccessLevel accessLevel;
    private Long resourceId;
    private String resourceType;
    private Boolean isInherited;
    private Long inheritedFromPermissionId;
    private Boolean isActive;
    private ZonedDateTime expiresAt;
    private String notes;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
