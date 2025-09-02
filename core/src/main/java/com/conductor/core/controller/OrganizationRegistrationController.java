package com.conductor.core.controller;

import com.conductor.core.dto.*;
import com.conductor.core.exception.OrganizationRegistrationException;
import com.conductor.core.model.application.Application;
import com.conductor.core.service.OrganizationApplicationService;
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

    private final OrganizationApplicationService organizationApplicationService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> registerOrganization(
            @Valid @RequestBody OrganizationRegistrationRequest request) {
        try {
            OrganizationRegistrationResult result = organizationApplicationService.registerOrganization(request);

            if (result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register organization");
            }

        } catch (OrganizationRegistrationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Form schema & submission endpoints for organization applications
    @GetMapping("/{applicationExternalId}/form")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FormSchemaResponse> getOrganizationForm(@PathVariable String applicationExternalId) {
        try {
            return ResponseEntity.ok(organizationApplicationService.getFormSchema(applicationExternalId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{applicationExternalId}/form")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setOrganizationForm(@PathVariable String applicationExternalId,
                                                                @RequestBody @Valid FormSchemaRequest request) {
        try {
            organizationApplicationService.setFormSchema(applicationExternalId, request);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{applicationExternalId}/form/submit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> submitOrganizationForm(@PathVariable String applicationExternalId,
                                                                   @RequestBody @Valid FormSubmissionRequest request) {
        try {
            organizationApplicationService.submitFormResult(applicationExternalId, request);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveOrganization(
            @Valid @RequestBody ApproveOrganizationRequest request) {
        try {
            boolean result = organizationApplicationService.approveOrganization(request);

            if (result) {
                return ResponseEntity.ok().build();

            } else {
                return ResponseEntity.badRequest().body("Failed to approve organization registration");
            }

        } catch (OrganizationRegistrationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Application>> getAllPendingOrganizations() {

        try {

            List<Application> pendingRegistrations =
                    organizationApplicationService.getAllOrganizationsWaitingForApproval();

            return ResponseEntity.ok(pendingRegistrations);

        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();         }
    }
}
