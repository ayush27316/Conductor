package com.conductor.core.controller;

import com.conductor.core.dto.*;
import com.conductor.core.exception.OrganizationRegistrationException;
import com.conductor.core.model.application.Application;
import com.conductor.core.service.OrganizationRegistrationService;
import lombok.RequiredArgsConstructor;
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
    public ResponseDTO<OrganizationRegistrationResult> registerOrganization(
            @Valid @RequestBody OrganizationRegistrationRequest request) {
        try {
            OrganizationRegistrationResult result = organizationRegistrationService.registerOrganization(request);

            if (result.isSuccess()) {
                return ResponseDTO.success("Organization registration successful", result);
            } else {
                return ResponseDTO.internalServerError("Failed to register organization");
            }

        } catch (OrganizationRegistrationException e) {
            return ResponseDTO.internalServerError(e.getMessage());
        }
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO<Map<String, Object>> approveOrganization(
            @Valid @RequestBody ApproveOrganizationRequest request) {
        try {
            boolean result = organizationRegistrationService.approveOrganization(request);

            if (result) {
                return ResponseDTO.success("Organization registration approved successfully");

            } else {
                return ResponseDTO.badRequest("Failed to approve organization registration");
            }

        } catch (OrganizationRegistrationException e) {
            return ResponseDTO.badRequest(e.getMessage());
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO<List<Application>> getAllPendingOrganizations() {

        try {

            List<Application> pendingRegistrations =
                    organizationRegistrationService.getAllOrganizationsWaitingForApproval();

            return ResponseDTO.success("Pending organization registrations retrieved successfully", pendingRegistrations);

        } catch (RuntimeException e){
            return ResponseDTO.internalServerError("Failed due to an internal error");         }
    }
}
