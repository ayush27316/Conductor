package com.conductor.core.service;

import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for checking user permissions and managing user-specific permission operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPermissionsService {

    private final PermissionService permissionService;

    /**
     * Check if a user has a specific privilege on a resource
     */
    public boolean hasPrivilege(User user, Resource resource, String resourceId, 
                               String privilege, AccessLevel requiredAccessLevel) {
        return permissionService.hasPrivilege(user, resource, resourceId, privilege, requiredAccessLevel);
    }

    /**
     * Check if a user has any of the specified privileges on a resource
     */
    public boolean hasAnyPrivilege(User user, Resource resource, String resourceId, 
                                  List<String> privileges, AccessLevel requiredAccessLevel) {
        for (String privilege : privileges) {
            if (hasPrivilege(user, resource, resourceId, privilege, requiredAccessLevel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a user has all of the specified privileges on a resource
     */
    public boolean hasAllPrivileges(User user, Resource resource, String resourceId, 
                                   List<String> privileges, AccessLevel requiredAccessLevel) {
        for (String privilege : privileges) {
            if (!hasPrivilege(user, resource, resourceId, privilege, requiredAccessLevel)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a user is an owner of a resource (has WRITE access to all privileges)
     */
    public boolean isOwner(User user, Resource resource, String resourceId) {
        // This would need to be implemented based on your business logic
        // For now, checking if user has WRITE access to CONFIG privilege
        return hasPrivilege(user, resource, resourceId, "config", AccessLevel.WRITE);
    }

    /**
     * Check if a user is an admin of a resource (has WRITE access to most privileges)
     */
    public boolean isAdmin(User user, Resource resource, String resourceId) {
        List<String> adminPrivileges = List.of("config", "operator", "audit");
        return hasAllPrivileges(user, resource, resourceId, adminPrivileges, AccessLevel.WRITE);
    }

    /**
     * Check if a user can view a resource (has READ access to VIEW privilege)
     */
    public boolean canView(User user, Resource resource, String resourceId) {
        return hasPrivilege(user, resource, resourceId, "view", AccessLevel.READ);
    }

    /**
     * Check if a user can edit a resource (has WRITE access to CONFIG privilege)
     */
    public boolean canEdit(User user, Resource resource, String resourceId) {
        return hasPrivilege(user, resource, resourceId, "config", AccessLevel.WRITE);
    }

    /**
     * Check if a user can manage users for a resource (has WRITE access to OPERATOR privilege)
     */
    public boolean canManageUsers(User user, Resource resource, String resourceId) {
        return hasPrivilege(user, resource, resourceId, "operator", AccessLevel.WRITE);
    }

    /**
     * Get a summary of user's permissions on a resource
     */
    public Map<String, String> getUserPermissionSummary(User user, Resource resource, String resourceId) {
        // This would return a map of privilege -> access level for the user on the resource
        // Implementation would depend on how you want to aggregate the permissions
        return null; // TODO: Implement based on your needs
    }
}
