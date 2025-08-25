package com.conductor.core.security;

import com.conductor.core.dto.permission.PermissionDTO;
import com.conductor.core.model.user.UserType;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // Get the UserPrincipal from authentication
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        // targetDomainObject should be the user type (String)
        String requiredUserType = (String) targetDomainObject;

        if(permission == null && userPrincipal.getUserType().equals(requiredUserType) ){
            return true;
        }
        // permission should be a List<PermissionDTO> containing the required permissions
        @SuppressWarnings("unchecked")
        List<PermissionDTO> requiredPermissions = (List<PermissionDTO>) permission;

        // Check if user type matches (if specified)
        if (requiredUserType != null && !requiredUserType.isEmpty()) {
            if (!requiredUserType.equals(userPrincipal.getUserType())) {
                return false;
            }
        }

        // Check if user has all required permissions
        if (requiredPermissions != null && !requiredPermissions.isEmpty()) {
            return hasAllRequiredPermissions(userPrincipal.getPermissions(), requiredPermissions);
        }

        return true; // If no specific requirements, allow access
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        // This overload can be used for resource-specific permissions
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        // targetType represents the user type requirement
        // targetId represents the specific resource ID
        // permission represents the required permission DTO list

        if (targetType != null && !targetType.equals(userPrincipal.getUserType())) {
            return false;
        }

        @SuppressWarnings("unchecked")
        List<PermissionDTO> requiredPermissions = (List<PermissionDTO>) permission;

        if (requiredPermissions != null && !requiredPermissions.isEmpty()) {
            return hasAllRequiredPermissions(userPrincipal.getPermissions(), requiredPermissions);
        }

        return true;
    }

    /**
     * Check if user has all required permissions
     */
    private boolean hasAllRequiredPermissions(List<PermissionDTO> userPermissions,
                                              List<PermissionDTO> requiredPermissions) {
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }

        for (PermissionDTO required : requiredPermissions) {
            if (!hasSpecificPermission(userPermissions, required)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if user has a specific permission with required access level
     */
    private boolean hasSpecificPermission(List<PermissionDTO> userPermissions,
                                          PermissionDTO requiredPermission) {
        for (PermissionDTO userPerm : userPermissions) {
            // Check if resource matches
            if (requiredPermission.getResourceName() != null &&
                    !requiredPermission.getResourceName().equals(userPerm.getResourceName())) {
                continue;
            }

            // Check if resource ID matches (if specified)
            if (requiredPermission.getResourceId() != null &&
                    !requiredPermission.getResourceId().equals(userPerm.getResourceId())) {
                continue;
            }

            // Check if user has all required privileges with sufficient access levels
            if (hasRequiredPrivileges(userPerm.getPermissions(), requiredPermission.getPermissions())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if user privileges meet the required access levels
     */
    private boolean hasRequiredPrivileges(Map<String, String> userPrivileges,
                                          Map<String, String> requiredPrivileges) {
        if (requiredPrivileges == null || requiredPrivileges.isEmpty()) {
            return true;
        }

        if (userPrivileges == null || userPrivileges.isEmpty()) {
            return false;
        }

        for (Map.Entry<String, String> required : requiredPrivileges.entrySet()) {
            String privilege = required.getKey();
            String requiredAccessLevel = required.getValue();

            String userAccessLevel = userPrivileges.get(privilege);

            if (userAccessLevel == null) {
                return false; // User doesn't have this privilege at all
            }

            // Compare access levels - you may need to implement access level hierarchy
            if (!hasRequiredAccessLevel(userAccessLevel, requiredAccessLevel)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if user's access level meets the required access level
     * Implement your access level hierarchy logic here
     */
    private boolean hasRequiredAccessLevel(String userAccessLevel, String requiredAccessLevel) {
        if (userAccessLevel == null || requiredAccessLevel == null) {
            return false;
        }

        // Simple string comparison - you should implement proper access level hierarchy
        // For example: ADMIN > WRITE > READ
        return userAccessLevel.equals(requiredAccessLevel) ||
                isHigherAccessLevel(userAccessLevel, requiredAccessLevel);
    }

    /**
     * Implement your access level hierarchy logic
     * This is a basic example - customize based on your AccessLevel enum
     */
    private boolean isHigherAccessLevel(String userLevel, String requiredLevel) {
        // Example hierarchy: ADMIN > WRITE > READ
        // Customize this based on your actual AccessLevel values

        if ("ADMIN".equals(userLevel)) {
            return true; // ADMIN has access to everything
        }

        if ("WRITE".equals(userLevel) && "READ".equals(requiredLevel)) {
            return true; // WRITE includes READ access
        }

        // Add more hierarchy rules based on your AccessLevel enum

        return false;
    }
}