package com.conductor.core.controler;

import com.conductor.core.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing permissions in the system.
 * Provides endpoints for granting, revoking, and checking permissions.
 */
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {
//
//    private final PermissionService permissionService;
//    private final UserService userService;
//    private final OrganizationService organizationService;
//    private final EventService eventService;
//
//    /**
//     * Grant a single permission to a user.
//     */
//    @PostMapping("/grant")
//    @PreAuthorize("hasRole('ADMIN') or @permissionService.hasPermission(authentication.principal, 'ORGANIZATION', 'OPERATORS', 'WRITE', #request.resourceId, #request.resourceType)")
//    public ResponseEntity<Permission> grantPermission(@Valid @RequestBody GrantPermissionRequestDTO request) {
//        try {
//            User user = userService.getUserById(request.getUserId());
//            User grantedBy = userService.getCurrentUser();
//
//            Permission permission = permissionService.grantPermission(
//                user,
//                request.getResource(),
//                request.getPrivilege(),
//                request.getAccessLevel(),
//                request.getResourceId(),
//                request.getResourceType(),
//                grantedBy,
//                request.getNotes()
//            );
//
//            log.info("Permission granted: {} {} {} to user {}",
//                    request.getResource(), request.getPrivilege(), request.getAccessLevel(), user.getUsername());
//
//            return ResponseEntity.ok(permission);
//        } catch (Exception e) {
//            log.error("Error granting permission: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Grant organization-level permissions to a user.
//     */
//    @PostMapping("/organization/{organizationId}/users/{userId}")
//    @PreAuthorize("hasRole('ADMIN') or @permissionService.hasPermission(authentication.principal, 'ORGANIZATION', 'OPERATORS', 'WRITE', #organizationId, 'Organization')")
//    public ResponseEntity<Map<String, Object>> grantOrganizationPermissions(
//            @PathVariable Long organizationId,
//            @PathVariable Long userId,
//            @Valid @RequestBody GrantOrganizationPermissionsRequestDTO request) {
//
//        try {
//            User user = userService.getUserById(userId);
//            Organization organization = organizationService.getOrganizationById(organizationId);
//            User grantedBy = userService.getCurrentUser();
//
//            List<Permission> permissions = permissionService.grantOrganizationPermissions(
//                user,
//                organization,
//                request.getPrivileges(),
//                request.getAccessLevel(),
//                grantedBy,
//                request.isOwner()
//            );
//
//            log.info("Organization permissions granted to user {}: {}",
//                    user.getUsername(), request.getPrivileges());
//
//            return ResponseEntity.ok(Map.of(
//                "message", "Organization permissions granted successfully",
//                "permissions", permissions,
//                "userId", userId,
//                "organizationId", organizationId
//            ));
//        } catch (Exception e) {
//            log.error("Error granting organization permissions: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Grant event-level permissions to a user.
//     */
//    @PostMapping("/event/{eventId}/users/{userId}")
//    @PreAuthorize("hasRole('ADMIN') or @permissionService.hasPermission(authentication.principal, 'EVENT', 'OPERATORS', 'WRITE', #eventId, 'Event')")
//    public ResponseEntity<Map<String, Object>> grantEventPermissions(
//            @PathVariable Long eventId,
//            @PathVariable Long userId,
//            @Valid @RequestBody GrantEventPermissionsRequestDTO request) {
//
//        try {
//            User user = userService.getUserById(userId);
//            Event event = eventService.getEventById(eventId);
//            User grantedBy = userService.getCurrentUser();
//
//            List<Permission> permissions = permissionService.grantEventPermissions(
//                user,
//                event,
//                request.getPrivileges(),
//                request.getAccessLevel(),
//                grantedBy,
//                request.isCreator()
//            );
//
//            log.info("Event permissions granted to user {}: {}",
//                    user.getUsername(), request.getPrivileges());
//
//            return ResponseEntity.ok(Map.of(
//                "message", "Event permissions granted successfully",
//                "permissions", permissions,
//                "userId", userId,
//                "eventId", eventId
//            ));
//        } catch (Exception e) {
//            log.error("Error granting event permissions: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Check if a user has a specific permission.
//     */
//    @GetMapping("/check")
//    public ResponseEntity<Map<String, Object>> checkPermission(@RequestParam Long userId,
//                                                             @RequestParam Resource resource,
//                                                             @RequestParam String privilege,
//                                                             @RequestParam AccessLevel accessLevel,
//                                                             @RequestParam(required = false) Long resourceId,
//                                                             @RequestParam(required = false) String resourceType) {
//        try {
//            User user = userService.getUserById(userId);
//
//            boolean hasPermission = permissionService.hasPermission(
//                user, resource, privilege, accessLevel, resourceId, resourceType);
//
//            return ResponseEntity.ok(Map.of(
//                "userId", userId,
//                "resource", resource,
//                "privilege", privilege,
//                "accessLevel", accessLevel,
//                "resourceId", resourceId,
//                "resourceType", resourceType,
//                "hasPermission", hasPermission
//            ));
//        } catch (Exception e) {
//            log.error("Error checking permission: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Get all permissions for a specific user.
//     */
//    @GetMapping("/users/{userId}")
//    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
//    public ResponseEntity<List<Permission>> getUserPermissions(@PathVariable Long userId) {
//        try {
//            List<Permission> permissions = permissionService.getUserPermissions(userId);
//            return ResponseEntity.ok(permissions);
//        } catch (Exception e) {
//            log.error("Error getting user permissions: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Get all permissions for a specific resource.
//     */
//    @GetMapping("/resources/{resource}/{resourceId}")
//    @PreAuthorize("hasRole('ADMIN') or @permissionService.hasPermission(authentication.principal, #resource, 'VIEW', 'READ', #resourceId, #resourceType)")
//    public ResponseEntity<List<Permission>> getResourcePermissions(
//            @PathVariable Resource resource,
//            @PathVariable Long resourceId,
//            @RequestParam String resourceType) {
//        try {
//            List<Permission> permissions = permissionService.getResourcePermissions(resource, resourceId);
//            return ResponseEntity.ok(permissions);
//        } catch (Exception e) {
//            log.error("Error getting resource permissions: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Revoke a permission from a user.
//     */
//    @DeleteMapping("/{permissionId}")
//    @PreAuthorize("hasRole('ADMIN') or @permissionService.hasPermission(authentication.principal, 'ORGANIZATION', 'OPERATORS', 'WRITE', null, null)")
//    public ResponseEntity<Map<String, Object>> revokePermission(
//            @PathVariable Long permissionId,
//            @RequestParam String reason) {
//        try {
//            User revokedBy = userService.getCurrentUser();
//            permissionService.revokePermission(permissionId, revokedBy, reason);
//
//            log.info("Permission {} revoked by {}: {}", permissionId, revokedBy.getUsername(), reason);
//
//            return ResponseEntity.ok(Map.of(
//                "message", "Permission revoked successfully",
//                "permissionId", permissionId,
//                "revokedBy", revokedBy.getUsername(),
//                "reason", reason
//            ));
//        } catch (Exception e) {
//            log.error("Error revoking permission: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Bulk revoke permissions for a user on a specific resource.
//     */
//    @DeleteMapping("/users/{userId}/resources/{resource}/{resourceId}")
//    @PreAuthorize("hasRole('ADMIN') or @permissionService.hasPermission(authentication.principal, 'ORGANIZATION', 'OPERATORS', 'WRITE', null, null)")
//    public ResponseEntity<Map<String, Object>> bulkRevokeUserPermissions(
//            @PathVariable Long userId,
//            @PathVariable Resource resource,
//            @PathVariable Long resourceId,
//            @RequestParam String reason) {
//        try {
//            User revokedBy = userService.getCurrentUser();
//            List<Permission> userPermissions = permissionService.getUserPermissions(userId);
//
//            // Revoke all permissions for this user on this resource
//            userPermissions.stream()
//                .filter(p -> p.getResource() == resource &&
//                           (p.getResourceId() == null || p.getResourceId().equals(resourceId)))
//                .forEach(p -> permissionService.revokePermission(p.getId(), revokedBy, reason));
//
//            log.info("Bulk revoked permissions for user {} on resource {} {} by {}",
//                    userId, resource, resourceId, revokedBy.getUsername());
//
//            return ResponseEntity.ok(Map.of(
//                "message", "Permissions revoked successfully",
//                "userId", userId,
//                "resource", resource,
//                "resourceId", resourceId,
//                "revokedBy", revokedBy.getUsername(),
//                "reason", reason
//            ));
//        } catch (Exception e) {
//            log.error("Error bulk revoking permissions: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Get permission statistics for a resource.
//     */
//    @GetMapping("/stats/resources/{resource}/{resourceId}")
//    @PreAuthorize("hasRole('ADMIN') or @permissionService.hasPermission(authentication.principal, #resource, 'VIEW', 'READ', #resourceId, #resourceType)")
//    public ResponseEntity<Map<String, Object>> getResourcePermissionStats(
//            @PathVariable Resource resource,
//            @PathVariable Long resourceId,
//            @RequestParam String resourceType) {
//        try {
//            List<Permission> permissions = permissionService.getResourcePermissions(resource, resourceId);
//
//            long activePermissions = permissions.stream().filter(p -> p.getIsActive()).count();
//            long inheritedPermissions = permissions.stream().filter(p -> p.getIsInherited()).count();
//            long expiredPermissions = permissions.stream().filter(p ->
//                p.getExpiresAt() != null && p.getExpiresAt().isBefore(java.time.ZonedDateTime.now())).count();
//
//            return ResponseEntity.ok(Map.of(
//                "resource", resource,
//                "resourceId", resourceId,
//                "totalPermissions", permissions.size(),
//                "activePermissions", activePermissions,
//                "inheritedPermissions", inheritedPermissions,
//                "expiredPermissions", expiredPermissions
//            ));
//        } catch (Exception e) {
//            log.error("Error getting resource permission stats: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
