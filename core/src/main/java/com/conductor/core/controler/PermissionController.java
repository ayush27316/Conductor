package com.conductor.core.controler;

import com.conductor.core.dto.GrantPermissionRequestDTO;
import com.conductor.core.dto.PermissionDTO;
import com.conductor.core.dto.RevokePermissionRequestDTO;
import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.user.User;
import com.conductor.core.security.UserPrincipal;
import com.conductor.core.service.PermissionService;
import com.conductor.core.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {

    private final PermissionService permissionService;
    private final UserService userService;

    /**
     * Grant permission to a user
     */
    @PostMapping("/grant")
    public ResponseEntity<PermissionDTO> grantPermission(
            @Valid @RequestBody GrantPermissionRequestDTO request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        User grantedBy = userService.getUserById(currentUser.getId());
        PermissionDTO permission = permissionService.grantPermission(request, grantedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    /**
     * Revoke permission from a user
     */
    @PostMapping("/revoke")
    public ResponseEntity<Void> revokePermission(
            @Valid @RequestBody RevokePermissionRequestDTO request) {
        
        permissionService.revokePermission(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all permissions for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PermissionDTO>> getUserPermissions(@PathVariable Long userId) {
        List<PermissionDTO> permissions = permissionService.getUserPermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    /**
     * Get all permissions for a specific resource
     */
    @GetMapping("/resource/{resource}/{resourceId}")
    public ResponseEntity<List<PermissionDTO>> getResourcePermissions(
            @PathVariable String resource,
            @PathVariable String resourceId) {
        
        Resource resourceEnum = Resource.valueOf(resource.toUpperCase());
        List<PermissionDTO> permissions = permissionService.getResourcePermissions(resourceEnum, resourceId);
        return ResponseEntity.ok(permissions);
    }

    /**
     * Get all users with a specific privilege on a resource
     */
    @GetMapping("/resource/{resource}/{resourceId}/privilege/{privilege}/users")
    public ResponseEntity<List<User>> getUsersWithPrivilege(
            @PathVariable String resource,
            @PathVariable String resourceId,
            @PathVariable String privilege) {
        
        Resource resourceEnum = Resource.valueOf(resource.toUpperCase());
        List<User> users = permissionService.getUsersWithPrivilege(resourceEnum, resourceId, privilege);
        return ResponseEntity.ok(users);
    }

    /**
     * Check if current user has a specific privilege
     */
    @GetMapping("/check/{resource}/{resourceId}/{privilege}/{accessLevel}")
    public ResponseEntity<Boolean> checkPrivilege(
            @PathVariable String resource,
            @PathVariable String resourceId,
            @PathVariable String privilege,
            @PathVariable String accessLevel,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        User user = userService.getUserById(currentUser.getId());
        Resource resourceEnum = Resource.valueOf(resource.toUpperCase());
        AccessLevel accessLevelEnum = AccessLevel.valueOf(accessLevel.toUpperCase());
        
        boolean hasPrivilege = permissionService.hasPrivilege(user, resourceEnum, resourceId, privilege, accessLevelEnum);
        return ResponseEntity.ok(hasPrivilege);
    }

    /**
     * Bulk grant permissions to multiple users
     */
    @PostMapping("/bulk-grant")
    public ResponseEntity<List<PermissionDTO>> bulkGrantPermissions(
            @Valid @RequestBody List<GrantPermissionRequestDTO> requests,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        User grantedBy = userService.getUserById(currentUser.getId());
        List<PermissionDTO> permissions = permissionService.bulkGrantPermissions(requests, grantedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(permissions);
    }

    /**
     * Get permissions expiring soon
     */
    @GetMapping("/expiring")
    public ResponseEntity<List<PermissionDTO>> getExpiringPermissions(
            @RequestParam(defaultValue = "7") int daysAhead) {
        
        List<PermissionDTO> permissions = permissionService.getPermissionsExpiringSoon(daysAhead);
        return ResponseEntity.ok(permissions);
    }

    /**
     * Deactivate expired permissions (admin only)
     */
    @PostMapping("/deactivate-expired")
    public ResponseEntity<Void> deactivateExpiredPermissions() {
        permissionService.deactivateExpiredPermissions();
        return ResponseEntity.noContent().build();
    }
}
