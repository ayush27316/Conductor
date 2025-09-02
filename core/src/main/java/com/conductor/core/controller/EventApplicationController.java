package com.conductor.core.controller;

import com.conductor.core.dto.*;
import com.conductor.core.exception.ApplicationNotFound;
import com.conductor.core.exception.EventNotFoundException;
import com.conductor.core.exception.EventRegistrationFailedException;
import com.conductor.core.model.application.Application;
import com.conductor.core.service.EventApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling event applications/reservations.
 * Provides endpoints for users to submit event reservations and
 * for organization operators to process them.
 */
@RestController
@RequestMapping("/api/event-applications")
@RequiredArgsConstructor
public class EventApplicationController {

    private final EventApplicationService eventApplicationService;

    /**
     * Submit an event application/reservation
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> submitEventApplication(
            @Valid @RequestBody EventApplicationRequest request) {
        try {
            EventApplicationResponse response = eventApplicationService.submitApplicationForEvent(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EventNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EventRegistrationFailedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit event application: " + e.getMessage());
        }
    }

    // Form schema & submission endpoints for event applications
    @GetMapping("/{applicationExternalId}/form")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> getEventForm(@PathVariable String applicationExternalId) {
        try {
            return ResponseEntity.ok(eventApplicationService.getFormSchema(applicationExternalId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{applicationExternalId}/form")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Void> setEventForm(@PathVariable String applicationExternalId,
                                                         @RequestBody @Valid FormSchemaRequest request) {
        try {
            eventApplicationService.setFormSchema(applicationExternalId, request);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{applicationExternalId}/form/submit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> submitEventForm(@PathVariable String applicationExternalId,
                                                            @RequestBody @Valid FormSubmissionRequest request) {
        try {
            eventApplicationService.submitFormResult(applicationExternalId, request);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Process an event application (approve/reject)
     */
    @PostMapping("/process")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> processEventApplication(
            @Valid @RequestBody ProcessEventApplicationRequest request) {
        try {
            String result = eventApplicationService.processEventApplication(request);
            return ResponseEntity.ok(result);
        } catch (ApplicationNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process event application: " + e.getMessage());
        }
    }

    /**
     * Get all pending event applications for an organization's events
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<List<Application>> getPendingEventApplications() {
        try {
            List<Application> pendingApplications = eventApplicationService.getPendingEventApplications();
            return ResponseEntity.ok(pendingApplications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get event applications for a specific event
     */
    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> getEventApplicationsByEvent(@PathVariable String eventId) {
        try {
            List<Application> applications = eventApplicationService.getEventApplicationsByEvent(eventId);
            return ResponseEntity.ok(applications);
        } catch (EventNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve event applications: " + e.getMessage());
        }
    }

    /**
     * Get current user's event applications
     */
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Application>> getUserEventApplications() {
        try {
            List<Application> applications = eventApplicationService.getUserEventApplications();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Add a comment to an event application
     */
    @PostMapping("/{applicationId}/comments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addComment(
            @PathVariable String applicationId,
            @RequestBody Map<String, String> request) {
        try {
            String comment = request.get("comment");
            if (comment == null || comment.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Comment is required");
            }
            
            eventApplicationService.addComment(applicationId, comment);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ApplicationNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add comment: " + e.getMessage());
        }
    }

    /**
     * Cancel an event application
     */
    @PostMapping("/{applicationId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelEventApplication(
            @PathVariable String applicationId,
            @RequestBody(required = false) Map<String, String> request) {
        try {
            String reason = request != null ? request.get("reason") : null;
            eventApplicationService.cancelEventApplication(applicationId, reason);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ApplicationNotFound e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific event application by ID
     */
    @GetMapping("/{applicationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getEventApplication(@PathVariable String applicationId) {
        try {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Get application by ID not yet implemented");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve event application: " + e.getMessage());
        }
    }
}
