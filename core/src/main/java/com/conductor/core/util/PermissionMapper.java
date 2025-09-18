package com.conductor.core.util;

import com.conductor.core.dto.permission.PermissionDTO;
import com.conductor.core.model.permission.Permission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
                        .resourceName(p.getResource().getResourceType().getName())
                        .resourceId(p.getResource().getExternalId())
                        .permissions(
                                p.getPermission().entrySet()
                                        .stream()
                                        .collect(Collectors.toMap(
                                                e -> e.getKey().getName(),   // privilege name
                                                e -> e.getValue().getName() // access level name
                                        ))
                        )
                        //.grantedAt(p.getGrantedAt())
                       // .grantedByUserExternalId(p.getGrantedBy().getExternalId().toString())
                       // .expiresAt(p.getExpiresAt())
                        .build())
                .toList();
    }


}





