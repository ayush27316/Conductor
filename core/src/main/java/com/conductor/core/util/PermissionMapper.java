package com.conductor.core.util;

import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.model.event.EventPrivilege;
import com.conductor.core.model.org.OrganizationPrivilege;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for mapping permissions between different formats
 * and handling JSON conversion for database storage
 */
@Component
@Slf4j
public class PermissionMapper {

    private final ObjectMapper objectMapper;

    public PermissionMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Convert permissions map to JSON string for database storage
     */
    public String permissionsToJson(Map<String, String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(permissions);
        } catch (JsonProcessingException e) {
            log.error("Error converting permissions to JSON", e);
            return "{}";
        }
    }

    /**
     * Convert JSON string from database to permissions map
     */
    public Map<String, String> jsonToPermissions(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to permissions", e);
            return new HashMap<>();
        }
    }

    /**
     * Create a permission map with organization privileges
     */
    public Map<String, String> createOrganizationPermissions(OrganizationPrivilege privilege, AccessLevel accessLevel) {
        Map<String, String> permissions = new HashMap<>();
        permissions.put(privilege.getName(), accessLevel.getName());
        return permissions;
    }

    /**
     * Create a permission map with event privileges
     */
    public Map<String, String> createEventPermissions(EventPrivilege privilege, AccessLevel accessLevel) {
        Map<String, String> permissions = new HashMap<>();
        permissions.put(privilege.getName(), accessLevel.getName());
        return permissions;
    }

    /**
     * Add a privilege to existing permissions map
     */
    public Map<String, String> addPrivilege(Map<String, String> existingPermissions, String privilege, AccessLevel accessLevel) {
        Map<String, String> newPermissions = new HashMap<>(existingPermissions);
        newPermissions.put(privilege, accessLevel.getName());
        return newPermissions;
    }

    /**
     * Remove a privilege from existing permissions map
     */
    public Map<String, String> removePrivilege(Map<String, String> existingPermissions, String privilege) {
        Map<String, String> newPermissions = new HashMap<>(existingPermissions);
        newPermissions.remove(privilege);
        return newPermissions;
    }

    /**
     * Check if a user has a specific privilege with required access level
     */
    public boolean hasPrivilege(Map<String, String> permissions, String privilege, AccessLevel requiredAccessLevel) {
        if (permissions == null || !permissions.containsKey(privilege)) {
            return false;
        }
        
        String userAccessLevel = permissions.get(privilege);
        if (userAccessLevel == null) {
            return false;
        }

        // WRITE access includes READ access
        if (requiredAccessLevel == AccessLevel.READ) {
            return AccessLevel.READ.getName().equals(userAccessLevel) || 
                   AccessLevel.WRITE.getName().equals(userAccessLevel);
        } else if (requiredAccessLevel == AccessLevel.WRITE) {
            return AccessLevel.WRITE.getName().equals(userAccessLevel);
        }
        
        return false;
    }

    /**
     * Merge multiple permission maps
     */
    public Map<String, String> mergePermissions(Map<String, String>... permissionMaps) {
        Map<String, String> merged = new HashMap<>();
        for (Map<String, String> permissions : permissionMaps) {
            if (permissions != null) {
                merged.putAll(permissions);
            }
        }
        return merged;
    }
}
