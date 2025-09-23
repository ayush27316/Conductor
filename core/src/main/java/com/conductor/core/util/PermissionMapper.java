package com.conductor.core.util;

import com.conductor.core.security.PrincipalPermission;
import com.conductor.core.model.permission.Permission;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * Utility class for mapping permissions between different formats
 * and handling JSON conversion for database storage
 */
@Component
public class PermissionMapper {

    public  static List<PrincipalPermission> toPermissionPrincipal(List<Permission> permissions) {
        if (permissions == null) return List.of();
        return permissions.stream()
                .map(p -> PrincipalPermission.builder()
                        .resourceType(p.getResource().getResourceType())
                        .resourceExternalId(p.getResource().getExternalId())
                        .permissions(p.getPermission())
                        .build())
                .toList();
    }
}





