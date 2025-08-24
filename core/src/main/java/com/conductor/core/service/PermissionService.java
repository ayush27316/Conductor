package com.conductor.core.service;

import com.conductor.core.dto.GrantPermissionRequestDTO;
import com.conductor.core.dto.PermissionDTO;
import com.conductor.core.dto.RevokePermissionRequestDTO;
import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.model.common.Permission;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.PermissionRepository;
import com.conductor.core.repository.UserRepository;
import com.conductor.core.util.PermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PermissionMapper permissionMapper;

    /**
     * Grant permissions to a user
     */
    public PermissionDTO grantPermission(GrantPermissionRequestDTO request, User grantedBy) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if permission already exists
        List<Permission> existingPermissions = permissionRepository
                .findByUserAndResourceAndResourceIdAndIsActiveTrue(
                        user, request.getResource().getResourceName(), request.getResourceId());

        Permission permission;
        if (!existingPermissions.isEmpty()) {
            // Update existing permission by adding new privilege
            permission = existingPermissions.get(0);
            Map<String, String> currentPermissions = permission.getPermissions();
            Map<String, String> updatedPermissions = permissionMapper.addPrivilege(
                    currentPermissions, request.getPrivilege(), request.getAccessLevel());
            permission.setPermissions(updatedPermissions);
        } else {
            // Create new permission
            Map<String, String> permissions = permissionMapper.createEventPermissions(
                    request.getPrivilege(), request.getAccessLevel());
            
            permission = Permission.builder()
                    .user(user)
                    .resourceName(request.getResource().getResourceName())
                    .resourceId(request.getResourceId())
                    .permissions(permissions)
                    .isActive(true)
                    .grantedAt(ZonedDateTime.now())
                    .grantedBy(grantedBy)
                    .expiresAt(request.getExpiresAt())
                    .build();
        }

        Permission savedPermission = permissionRepository.save(permission);
        return mapToDTO(savedPermission);
    }

    /**
     * Revoke a specific privilege from a user
     */
    public void revokePermission(RevokePermissionRequestDTO request) {
        List<Permission> permissions = permissionRepository
                .findByUserAndResourceAndResourceIdAndIsActiveTrue(
                        userRepository.findById(request.getUserId()).orElse(null),
                        request.getResource().getResourceName(), 
                        request.getResourceId());

        for (Permission permission : permissions) {
            Map<String, String> currentPermissions = permission.getPermissions();
            Map<String, String> updatedPermissions = permissionMapper.removePrivilege(
                    currentPermissions, request.getPrivilege());
            
            if (updatedPermissions.isEmpty()) {
                // If no privileges left, deactivate the permission
                permission.setIsActive(false);
            } else {
                permission.setPermissions(updatedPermissions);
            }
            permissionRepository.save(permission);
        }
    }

    /**
     * Check if user has a specific privilege
     */
    public boolean hasPrivilege(User user, Resource resource, String resourceId, 
                               String privilege, AccessLevel requiredAccessLevel) {
        List<Permission> permissions = permissionRepository
                .findByUserAndResourceAndResourceIdAndIsActiveTrue(user, resource.getResourceName(), resourceId);

        for (Permission permission : permissions) {
            if (permission.getExpiresAt() != null && permission.getExpiresAt().isBefore(ZonedDateTime.now())) {
                continue; // Skip expired permissions
            }
            
            if (permissionMapper.hasPrivilege(permission.getPermissions(), privilege, requiredAccessLevel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all permissions for a user
     */
    public List<PermissionDTO> getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Permission> permissions = permissionRepository.findByUserAndIsActiveTrue(user);
        return permissions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all permissions for a specific resource
     */
    public List<PermissionDTO> getResourcePermissions(Resource resource, String resourceId) {
        List<Permission> permissions = permissionRepository
                .findByResourceAndResourceIdAndIsActiveTrue(resource.getResourceName(), resourceId);
        
        return permissions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all users with a specific privilege on a resource
     */
    public List<User> getUsersWithPrivilege(Resource resource, String resourceId, String privilege) {
        return permissionRepository.findUsersWithPrivilege(resource.getResourceName(), resourceId, privilege);
    }

    /**
     * Bulk grant permissions to multiple users
     */
    public List<PermissionDTO> bulkGrantPermissions(List<GrantPermissionRequestDTO> requests, User grantedBy) {
        List<PermissionDTO> results = new ArrayList<>();
        
        for (GrantPermissionRequestDTO request : requests) {
            try {
                PermissionDTO permission = grantPermission(request, grantedBy);
                results.add(permission);
            } catch (Exception e) {
                log.error("Failed to grant permission for user {}: {}", request.getUserId(), e.getMessage());
            }
        }
        
        return results;
    }

    /**
     * Deactivate expired permissions
     */
    public void deactivateExpiredPermissions() {
        List<Permission> expiredPermissions = permissionRepository
                .findExpiredPermissions(ZonedDateTime.now());
        
        for (Permission permission : expiredPermissions) {
            permission.setIsActive(false);
            permissionRepository.save(permission);
        }
        
        log.info("Deactivated {} expired permissions", expiredPermissions.size());
    }

    /**
     * Get permissions expiring soon
     */
    public List<PermissionDTO> getPermissionsExpiringSoon(int daysAhead) {
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = start.plusDays(daysAhead);
        
        List<Permission> expiringPermissions = permissionRepository
                .findPermissionsExpiringBetween(start, end);
        
        return expiringPermissions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Map Permission entity to DTO
     */
    private PermissionDTO mapToDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .userId(permission.getUser().getId())
                .username(permission.getUser().getUsername())
                .resource(permission.getResourceName())
                .resourceId(permission.getResourceId())
                .privilege(null) // Not applicable with Map approach
                .accessLevel(null) // Not applicable with Map approach
                .isActive(permission.getIsActive())
                .grantedAt(permission.getGrantedAt())
                .grantedByUserId(permission.getGrantedBy() != null ? permission.getGrantedBy().getId() : null)
                .grantedByUsername(permission.getGrantedBy() != null ? permission.getGrantedBy().getUsername() : null)
                .expiresAt(permission.getExpiresAt())
                .build();
    }
}
