package com.conductor.core.controller.api.organization;

import com.conductor.core.dto.*;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.user.User;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.security.UserPrincipal;
import com.conductor.core.security.fiber.FiberPermissionEvaluator;
import com.conductor.core.security.fiber.FiberPermissionEvaluatorChain;
import com.conductor.core.service.OrganizationApplicationAndOnboardingService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationRegistrationController {

    private final OrganizationApplicationAndOnboardingService organizationApplicationAndOnboardingService;
    private final FiberPermissionEvaluator permissionEvaluator;

    @PostMapping("/apply")
    public ResponseEntity<?> apply(
            Authentication auth,
            @RequestBody OrganizationApplicationRequest request) {

        String applicationExternalId = organizationApplicationAndOnboardingService.apply(
                (UserPrincipal) auth.getPrincipal(),
                request);

        Map<String, String> body = Map.of("application_id", applicationExternalId);
        return ResponseEntity.ok().body(body);

    }


    @PutMapping("/applications/{application-id}/approve")
    public ResponseEntity<?> approveApplication(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(max = 100)
            String applicationExternalId,
            Authentication auth) {

        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                .addRole(UserRole.ADMIN)
                .evaluate(auth);


        organizationApplicationAndOnboardingService.approve(
                (UserPrincipal) auth.getPrincipal(),
                applicationExternalId
        );

        return ResponseEntity.ok().build();
    }


    @PutMapping("/applications/{application-id}/reject")
    public ResponseEntity<?> rejectApplication(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            @RequestParam @NotBlank(message = "Rejection reason cannot be blank")
            @Size(max = 200, message = "Reason must be at most 200 characters")
            String reason,
            Authentication auth) {

        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                .addRole(UserRole.ADMIN)
                .evaluate(auth);

        organizationApplicationAndOnboardingService.reject(
                (UserPrincipal) auth.getPrincipal(),
                applicationExternalId,
                reason
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/applications/{application-id}")
    public ResponseEntity<?> cancelApplication(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            Authentication auth) {

        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                .addPermission(applicationExternalId,ResourceType.APPLICATION,null)
                .evaluate(auth);

        organizationApplicationAndOnboardingService.cancel((UserPrincipal) auth.getPrincipal(), applicationExternalId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/applications/{application-id}/comments")
    public ResponseEntity<?> comment(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            @RequestParam @NotBlank(message = "Comment cannot be blank")
            @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
            String comment,
            Authentication auth) {


        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                //either an ADMIN
                .orRole(UserRole.ADMIN)
                //or the user who submitted the application
                .orPermission(applicationExternalId,ResourceType.APPLICATION,null)
                .evaluate(auth);

        organizationApplicationAndOnboardingService.comment(
                (UserPrincipal) auth.getPrincipal(),
                applicationExternalId,
                comment
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/applications/pending")
    public ResponseEntity<List<ApplicationDTO>> getAllPendingApplications(Authentication auth) {


        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                .orRole(UserRole.ADMIN)
                .evaluate(auth);

        List<Application> pendingRegistrations =
                organizationApplicationAndOnboardingService.getAllOrganizationsWaitingForApproval();
        return ResponseEntity.ok(null);
    }
}