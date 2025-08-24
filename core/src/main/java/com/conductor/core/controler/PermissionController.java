package com.conductor.core.controler;

import com.conductor.core.dto.permission.GrantPermissionRequestDTO;
import com.conductor.core.dto.permission.GrantPermissionResponseDTO;
import com.conductor.core.dto.permission.PermissionDTO;
import com.conductor.core.dto.permission.RevokePermissionRequestDTO;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Resource;
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
    public ResponseEntity<GrantPermissionResponseDTO> grantPermission(
            @Valid @RequestBody GrantPermissionRequestDTO request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        GrantPermissionResponseDTO responseDTO = permissionService.grantPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Revoke permission from a user
     */
    @PostMapping("/revoke")
    public ResponseEntity<Void> revokePermission(
            @Valid @RequestBody RevokePermissionRequestDTO request) {
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all permissions for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PermissionDTO>> getUserPermissions(@PathVariable String userExternalId) {
        List<PermissionDTO> permissions = permissionService.getUserPermissions(userExternalId);
        return ResponseEntity.ok(permissions);
    }
}
