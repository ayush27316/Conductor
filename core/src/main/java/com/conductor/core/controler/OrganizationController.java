package com.conductor.core.controler;

import com.conductor.core.dto.OrganizationDTO;
import com.conductor.core.model.ticket.TicketReservation;
import com.conductor.core.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/organizations")
@CrossOrigin(origins = "*")
public class OrganizationController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    private OrganizationService organizationService;

    /**
     * Register a new organization for approval
     * This creates a pending ticket reservation for organization onboarding
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerOrganization(@Valid @RequestBody OrganizationDTO organizationDTO) {
        try {
            organizationService.registerOrganization(organizationDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Organization registration submitted successfully. Awaiting approval.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register organization: " + e.getMessage());
        }
    }


    /**
     * Approve a pending organization registration
     * This endpoint is typically used by conductor organization operators
     */
    @PostMapping("/approve/{reservation-id}")
    public ResponseEntity<String> approveOrganization(@PathVariable(value = "reservation-id", required = true) String reservationExternalId) {
        logger.info("Attempting to approve organization with reservation ID: {}", reservationExternalId);

        if (reservationExternalId == null || reservationExternalId.trim().isEmpty()) {
            logger.warn("Empty or null reservation ID provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Reservation ID cannot be empty");
        }

        try {
            boolean success = organizationService.approveOrganization(reservationExternalId);
            //exception util
            if (success) {
                logger.info("Organization approved and onboarding initiated successfully for reservation: {}", reservationExternalId);
                return ResponseEntity.ok("Organization approved and onboarding initiated successfully.");
            } else {
                logger.warn("Organization approval failed - reservation not for organization onboarding: {}", reservationExternalId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Approval failed: Reservation is not for organization onboarding");
            }
        } catch (EntityNotFoundException e) {
            logger.error("Reservation not found: {}", reservationExternalId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reservation not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during organization approval for reservation: {}", reservationExternalId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to approve organization: " + e.getMessage());
        }
    }

    /**
     * Get all organizations waiting for approval
     * This endpoint is typically used by conductor organization operators
     */
    @GetMapping("/waiting-for-approval")
    public ResponseEntity<?> getPendingOrganizations() {
        logger.info("Fetching pending organization approvals");
        try {
            List<TicketReservation> pendingReservations = organizationService.getAllOrganizationsWaitingForApproval();
            logger.info("Found {} pending organization approvals", pendingReservations.size());
            return ResponseEntity.ok(pendingReservations);
        } catch (Exception e) {
            logger.error("Error fetching pending organizations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch pending organizations: " + e.getMessage());
        }
    }

}