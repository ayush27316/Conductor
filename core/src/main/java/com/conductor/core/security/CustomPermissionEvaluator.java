package com.conductor.core.security;

import com.conductor.core.model.Option;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.permission.Privilege;
import com.conductor.core.model.user.User;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // Get the UserPrincipal from authentication
        User userPrincipal = (User) auth.getPrincipal();

        // targetDomainObject should be the user role(String)
        String requiredRole = (String) targetDomainObject;

        if(permission == null && userPrincipal.getRole().equals(requiredRole) ){
            return true;
        }
        List<Permission> requiredPermissions = (List<Permission>) permission;

        // Check if user type matches (if specified)
        if (requiredRole != null && !requiredRole.isEmpty()) {
            if (!requiredRole.equals(userPrincipal.getRole())) {
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
        User userPrincipal = (User) auth.getPrincipal();
        String externalId = (String) targetId;

        Optional<ResourceType> resourceType = Option.fromName(ResourceType.class, targetType);
        if (resourceType.isEmpty()) {
            throw new IllegalArgumentException("resource type not found: " + targetType);
        }

        if (!(permission instanceof Map<?, ?> permMap)) {
            throw new IllegalArgumentException("permission must be a Map<String, String>");
        }

        @SuppressWarnings("unchecked")
        Map<String, String> requiredPermission = (Map<String, String>) permMap;

        for (Permission p : userPrincipal.getPermissions()) {
            if (p.getResource() != null && externalId.equals(String.valueOf(p.getResource().getExternalId()))) {
                Map<Privilege, AccessLevel> userPermission = p.getPermission();
                if (userPermission == null) continue;

                boolean allMatched = true;
                for (Map.Entry<String, String> required : requiredPermission.entrySet()) {
                    boolean matched = userPermission.entrySet().stream()
                            .anyMatch(e -> e.getKey().getName().equals(required.getKey())
                                    && e.getValue().getName().equals(required.getValue()));

                    if (!matched) {
                        allMatched = false;
                        break;
                    }
                }
                return allMatched; // found resource → return immediately
            }
        }
        return false; // no matching resource found
    }

//
//    @Override
//    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
//
//        User userPrincipal = (User) auth.getPrincipal();
//
//        String externalId = (String) targetId;
//
//        Optional<ResourceType> resourceType = Option.fromName(ResourceType.class, targetType);
//
//        if(resourceType.isEmpty())
//        {
//            throw new IllegalArgumentException("resource type not found: " + targetType);
//
//        }
//
//        @SuppressWarnings("unchecked")
//        Map<String, String> requiredPermission = (Map<String, String>) permission;
//
//        //extract permissions for this resource form auth
//        List<Permission> userPermissions = userPrincipal.getPermissions();
//
//        boolean userHasAccessToResource  = false;
//        boolean userHasRequiredPermission = true;
//
//        for(Permission p: userPermissions){
//            if(externalId.equals(p.getResource().getExternalId().toString()))
//            {
//                Map<Privilege, AccessLevel> userPermission = p.getPermission();
//
//
//                for(String key: requiredPermission.keySet())
//                {
//                    boolean permissionMatched = false;
//
//                    for(Privilege privilege : userPermission.keySet())
//                    {
//                        if(privilege.getName().equals(key))
//                        {
//                            if(userPermission.get(privilege).getName().equals(requiredPermission.get(key)))
//                            {
//                                   permissionMatched = true;
//                            }
//                        }
//                    }
//
//                    if(permissionMatched == false)
//                    {
//                        userHasRequiredPermission = false;
//                        break;
//                    }
//                }
//                userHasAccessToResource = true;
//                break;
//            }
//
//        }
//
//        return userHasRequiredPermission && userHasAccessToResource;
//    }

    /**
     * Check if user has all required permissions
     */
    private boolean hasAllRequiredPermissions(List<Permission> userPermissions,
                                              List<Permission> requiredPermissions) {
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }

        for (Permission required : requiredPermissions) {
            if (!hasSpecificPermission(userPermissions, required)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if user has a specific permission with required access level
     */
    private boolean hasSpecificPermission(List<Permission> userPermissions,
                                          Permission requiredPermission) {
//        for (Permission userPerm : userPermissions) {
//            // Check if resourceType matches
//            if (requiredPermission.getResourceName() != null &&
//                    !requiredPermission.getResourceName().equals(userPerm.getResourceName())) {
//                continue;
//            }
//
//            // Check if resourceType ID matches (if specified)
//            if (requiredPermission.getResourceId() != null &&
//                    !requiredPermission.getResourceId().equals(userPerm.getResourceId())) {
//                continue;
//            }
//
//            // Check if user has all required privileges with sufficient access levels
//            if (hasRequiredPrivileges(userPerm.getPrivileges(), requiredPermission.getPrivileges())) {
//                return true;
//            }
//        }

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
                return false; // User doesn'clause have this privilege at all
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