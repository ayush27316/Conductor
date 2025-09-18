package com.conductor.core.controller.api.event;

import com.conductor.core.dto.ApplicationDTO;
import com.conductor.core.dto.FormResponse;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.file.File;
import com.conductor.core.model.user.User;
import com.conductor.core.service.EventApplicationService;
import com.conductor.core.service.FileService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static com.conductor.core.security.fiber.FiberPermissionEvaluator.hasPermission;

import java.util.Arrays;
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
    private final FileService fileService;

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


    //an operator with event level permission
    //@PreAuthorize("hasPermission(#event-id, 'event', {'application':'write'})") or
    //an operator with organization level permission
    //@PreAuthorize("hasPermission(#organization-id, 'organization', {'event':'write'})")
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


    //an operator with event level permission
    //@PreAuthorize("hasPermission(#event-id, 'event', {'application':'write'})") or
    //an operator with organization level permission
    //@PreAuthorize("hasPermission(#organization-id, 'organization', {'event':'write'})")
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

//    @PreAuthorize("hasPermission(#application-id, 'application', null)")
    @DeleteMapping("/applications/{application-id}")
    public ResponseEntity<?> cancelApplication(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            Authentication auth) {

        eventApplicationService.cancelEventApplication(applicationExternalId, (User) auth.getPrincipal());
        return ResponseEntity.ok().build();
    }

    // the user who submitted the application
    //@PreAuthorize("hasPermission(#application-id, 'application', null)") or
    //an operator with event level permission
    //@PreAuthorize("hasPermission(#event-id, 'event', {'application':'write'})") or
    //an operator with organization level permission
    //@PreAuthorize("hasPermission(#organization-id, 'organization', {'event':'write'})")
    @PostMapping("/applications/{application-id}/comments")
    public ResponseEntity<?> comment(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            @RequestParam(name="comment") @NotBlank(message = "Comment cannot be blank")
            @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
            String comment,
            Authentication authentication) {

        System.out.println(comment);
        eventApplicationService.comment(
                (User)authentication.getPrincipal(),
                applicationExternalId,
                comment
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //an operator with event level permission
    //@PreAuthorize("hasPermission(#event-id, 'event', {'application':'write'})") or
    //an operator with organization level permission
    //@PreAuthorize("hasPermission(#organization-id, 'organization', {'event':'write'})")
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

    //@PreAuthorize("hasPermission(#application-id, 'application', null)")
    @PostMapping("/{application-id}/files/")
    public ResponseEntity<?> uploadFile(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String eventApplicationExternalId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        eventApplicationService.storeFile(file,eventApplicationExternalId, (User) authentication.getPrincipal());
        return ResponseEntity.ok().build();
    }
    // the user who submitted the application
    //@PreAuthorize("hasPermission(#application-id, 'application', null)") or
    //an operator with event level permission
    //@PreAuthorize("hasPermission(#event-id, 'event', {'application':'write'})") or
    //an operator with organization level permission
    //@PreAuthorize("hasPermission(#organization-id, 'organization', {'event':'write'})")
    @GetMapping("/{application-id}/files/{file-id}")
    public ResponseEntity<byte[]> downloadFile(
                    @PathVariable("application-id")
                    @NotBlank(message = "Application Id is required")
                    @Size(min = 36, max = 36)
                    String applicationExternalId,
                    @PathVariable("file-id")
                    @NotBlank(message = "File Id is required")
                    @Size(min = 36, max = 36)
                    String fileExternalId,
                    Authentication auth
                    )
    {
        //must be an event applciation
        //then application_id = organization organization_hash.event_hash.applicaiton_hash.sign

        String[] parts = applicationExternalId.split("\\.");
        List<String> result = Arrays.asList(parts);


        hasPermission(auth,applicationExternalId, ResourceType.APPLICATION,null);


        File file = fileService.getFile(fileExternalId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getData());
    }
}
