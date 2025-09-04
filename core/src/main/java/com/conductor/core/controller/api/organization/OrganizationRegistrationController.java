package com.conductor.core.controller.api.organization;

import com.conductor.core.dto.*;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.user.User;
import com.conductor.core.service.OrganizationApplicationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationRegistrationController {

    private final OrganizationApplicationService organizationApplicationService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> register(
            Authentication auth,
            @Valid @RequestBody OrganizationRegistrationRequest request) {

        String applicationExternalId = organizationApplicationService.register(
                (User) auth.getPrincipal(),
                request);

        Map<String, String> body = Map.of("registration_id", applicationExternalId);
        return ResponseEntity.ok().body(body);

    }


    //@PreAuthorize("hasPermission(#application-id, 'application', null)")
    @PutMapping("/applications/{application-id}/approve")
    public ResponseEntity<?> approveApplication(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            Authentication authentication) {

        organizationApplicationService.approve(
                (User) authentication.getPrincipal(),
                applicationExternalId
        );

        return ResponseEntity.ok().build();
    }

    //@PreAuthorize("hasPermission(#application-id, 'application', null)")
    @PutMapping("/applications/{application-id}/reject")
    public ResponseEntity<?> rejectApplication(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            @RequestParam @NotBlank(message = "Rejection reason cannot be blank")
            @Size(max = 200, message = "Reason must be at most 200 characters")
            String reason,
            Authentication authentication) {

        organizationApplicationService.reject(
                (User) authentication.getPrincipal(),
                applicationExternalId,
                reason
        );

        return ResponseEntity.ok().build();
    }

    //@PreAuthorize("hasPermission(#application-id, 'application', {cancel:'write'})")
    @DeleteMapping("/applications/{application-id}")
    public ResponseEntity<?> cancelApplication(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId) {

        organizationApplicationService.cancel(applicationExternalId);
        return ResponseEntity.ok().build();
    }

    //@PreAuthorize("hasPermission(#application-id, 'application', null)")
    @PostMapping("/applications/{application-id}/comments")
    public ResponseEntity<?> comment(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            @RequestParam @NotBlank(message = "Comment cannot be blank")
            @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
            String comment,
            Authentication authentication) {

        organizationApplicationService.comment(
                (User) authentication.getPrincipal(),
                applicationExternalId,
                comment
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/applications/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Application>> getAllPendingOrganizations() {
        List<Application> pendingRegistrations =
                organizationApplicationService.getAllOrganizationsWaitingForApproval();
        return ResponseEntity.ok(pendingRegistrations);
    }
}