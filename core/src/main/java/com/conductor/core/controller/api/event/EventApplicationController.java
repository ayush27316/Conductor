package com.conductor.core.controller.api.application;

import com.conductor.core.dto.ApplicationDTO;
import com.conductor.core.dto.FormResponse;
import com.conductor.core.model.user.User;
import com.conductor.core.service.EventApplicationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 *  Controller for managing event applications.
 * <p>
 * This controller provides endpoints for users to apply to events,
 * submit application forms, and for operators to manage applications
 * </p>
 */
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Validated
@Tag(name = "Event Applications", description = "APIs for managing event applications and reservations")
public class EventApplicationController {

    private final EventApplicationService eventApplicationService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{event-id}/apply")
    public ResponseEntity<?> applyForEvent(
            @PathVariable("event-id")
            @NotBlank(message = "Event Id is required")
            @Size(min = 36, max = 36)
            String eventExternalId,
            @RequestBody FormResponse formResponse,
            Authentication authentication) {

         String applicationExternalId = eventApplicationService.apply(
                        (User)authentication.getPrincipal(),
                        eventExternalId,
                        formResponse.getFormResponse());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //@PreAuthorize("hasPermission(#application-id, 'application', null)")
    @PutMapping("/applications/{application-id}/approve")
    public ResponseEntity<?> approveApplication(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            Authentication authentication) {

        eventApplicationService.approveEventApplication(
                (User)authentication.getPrincipal(),
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

        eventApplicationService.rejectEventApplication(
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

        eventApplicationService.cancelEventApplication(applicationExternalId);
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

        eventApplicationService.comment(
                (User)authentication.getPrincipal(),
                applicationExternalId,
                comment
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //@PreAuthorize("hasPermission(#application-id, 'application', null)")
    @GetMapping("/{event-id}/applications")
    public ResponseEntity<?> getEventApplications(
            @Parameter(description = "External ID of the event", required = true)
            @PathVariable("event-id") @NotBlank String eventExternalId) {

        List<ApplicationDTO> applications = eventApplicationService.getEventApplications(eventExternalId);

        return ResponseEntity.ok(applications);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{event-id}/form")
    public ResponseEntity<?> getEventForm(
            @Parameter(description = "External ID of the event", required = true)
            @PathVariable("event-id") @NotBlank String eventExternalId) {

        String form = eventApplicationService.getEventForm(eventExternalId);

        return ResponseEntity.ok(Map.of("form",form));
    }

}
