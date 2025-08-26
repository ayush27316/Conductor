package com.conductor.core.util;

import com.conductor.core.dto.permission.PermissionDTO;
import com.conductor.core.model.permission.Permission;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility class for mapping permissions between different formats
 * and handling JSON conversion for database storage
 */

//@Mapper(componentModel = "spring")
@Component
public class PermissionMapper {

    public  static List<PermissionDTO> toPermissionDTOs(List<Permission> permissions) {
        if (permissions == null) return List.of();
        return permissions.stream()
                .map(p -> PermissionDTO.builder()
                        .userExternalId(p.getUser().getExternalId())
//                        .resourceName(p.getResourceName())
//                        .resourceId(p.getResourceId())
                        .permissions(p.getPrivileges())
                        .grantedAt(p.getGrantedAt())
                        .grantedByUserExternalId(p.getGrantedBy().getExternalId())
                        .expiresAt(p.getExpiresAt())
                        .build())
                .toList();
    }

}



