package com.conductor.core.controller;

import com.conductor.core.dto.ApproveOrganizationRequest;
import com.conductor.core.dto.ErrorDetails;
import com.conductor.core.dto.OrganizationRegistrationRequest;
import com.conductor.core.dto.OrganizationRegistrationResult;
import com.conductor.core.exception.OrganizationRegistrationException;
import com.conductor.core.model.org.OrganizationRegistration;
import com.conductor.core.service.OrganizationRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationRegistrationController {

    private final OrganizationRegistrationService organizationRegistrationService;



    @PostMapping("/register")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Register a new organization",
            description = "Submit a new organization registration request. The user must be authenticated and cannot have any pending registrations."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Organization registration submitted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrganizationRegistrationResult.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data, user has pending registration, or any other internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )
            )
    })
    public ResponseEntity<OrganizationRegistrationResult> registerOrganization(
            @Parameter(description = "Organization registration request details", required = true)
            @Valid @RequestBody OrganizationRegistrationRequest request) {

        try {

            OrganizationRegistrationResult result = organizationRegistrationService
                    .registerOrganization(request);

            if (result.isSuccess()) {
                 return ResponseEntity.status(HttpStatus.CREATED).body(result);
            } else {
               return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
            }

        } catch (OrganizationRegistrationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(OrganizationRegistrationResult.builder()
                            .success(false)
                            .message("Registration processing failed. Please try again later.")
                            .build());
        } catch (IllegalArgumentException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(OrganizationRegistrationResult.builder()
                            .success(false)
                            .message(e.getMessage() + "Registration processing failed.")
                            .build());
        }
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Approve an organization registration",
            description = "Approve a pending organization registration by registration ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Organization registration approved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration ID or organization already approved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registration not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )
            )
    })
    public ResponseEntity<?> approveOrganization(
            @Parameter(description = "Organization approval request details", required = true)
            @Valid @RequestBody ApproveOrganizationRequest request) {

        try {
            boolean result = organizationRegistrationService.approveOrganization(request);

            if (result) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Organization registration approved successfully",
                        "registrationId", request.getRegistrationId()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Failed to approve organization registration"
                ));
            }

        } catch (OrganizationRegistrationException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get all organizations waiting for approval",
            description = "Retrieve a list of all organization registrations with pending status. Only admins and operators can access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of pending organization registrations retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrganizationRegistration.class))
                    )
            )
    })
    public ResponseEntity<?> getAllPendingOrganizations() {

        List<OrganizationRegistration> pendingRegistrations =
                    organizationRegistrationService.getAllOrganizationsWaitingForApproval();

        return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", pendingRegistrations,
                    "count", pendingRegistrations.size(),
                    "message", "Pending organization registrations retrieved successfully"
            ));
    }
}


//    /**
//     * Approve a pending organization registration
//     * This endpoint is typically used by conductor organization operators
//     */
//    @PostMapping("/approve")
//    public ResponseEntity<String> approveOrganization(@Valid @RequestBody ApproveOrganizationRequest request) {
//
//        boolean success = organizationRegistrationService.approveOrganization(request);
//            if (success) {
//                return ResponseEntity.ok("Organization approved and onboarding initiated successfully.");
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Approval failed. Please try again");
//            }
//    }


//    /**
//     * Get all organizations waiting for approval
//     * This endpoint is typically used by conductor organization operators
//     */
//  //  @PreAuthorize("hasPermission('OPERATOR', @permissionService.createRequiredPermissions('ORGANIZATION', #organizationExternalId, {'event':'write' }))")
//    @GetMapping("/waiting-for-approval")
//    public ResponseEntity<?> getPendingOrganizations() {
//       logger.info("Fetching pending organization approvals");
//        try {
//            List<TicketReservation> pendingReservations = organizationRegistrationService.getAllOrganizationsWaitingForApproval();
//            logger.info("Found {} pending organization approvals", pendingReservations.size());
//            return ResponseEntity.ok(pendingReservations);
//        } catch (Exception e) {
//            logger.error("Error fetching pending organizations", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to fetch pending organizations: " + e.getMessage());
//        }
//    }

//
//    // Example 2: Check specific permission without user type restriction
//    @PreAuthorize("hasPermission(null, @permissionService.createRequiredPermissions('USER_MANAGEMENT', null, {'READ': 'READ'}))")
//    @GetMapping("/users")
//    public String getUsers() {
//        return "Users list";
//    }
//
//    // Example 3: Check both user type and specific permissions
//    @PreAuthorize("hasPermission('MANAGER', @permissionService.createRequiredPermissions('PROJECT_MANAGEMENT', #projectId, {'WRITE': 'WRITE'}))")
//    @PostMapping("/projects/{projectId}/update")
//    public String updateProject(@PathVariable String projectId) {
//        return "Project updated";
//    }
//
//    // Example 4: Using the second overload with resource ID
//    @PreAuthorize("hasPermission(#resourceId, 'ADMIN', @permissionService.createRequiredPermissions('RESOURCE_MANAGEMENT', #resourceId, {'DELETE': 'DELETE'}))")
//    @DeleteMapping("/resources/{resourceId}")
//    public String deleteResource(@PathVariable String resourceId) {
//        return "Resource deleted";
//    }
