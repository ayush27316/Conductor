package com.conductor.core.dto.permission;

import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.model.org.OrganizationPrivilege;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for granting organization-level permissions to a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantOrganizationPermissionsRequestDTO {

    @NotNull(message = "Organization is required")
    private Long organizationId;

    @NotNull(message = "Privileges are required")
    @Size(min = 1, message = "At least one privilege must be specified")
    private List<OrganizationPrivilege> privileges;

    @NotNull(message = "Access level is required")
    private AccessLevel accessLevel;

    @Builder.Default
    private boolean owner = false;

    private String notes;
}
