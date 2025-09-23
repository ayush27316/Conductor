package com.conductor.core.controller.api.event;

import com.conductor.core.dto.ApplicationDTO;
import com.conductor.core.dto.FormResponse;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.event.EventPrivilege;
import com.conductor.core.model.file.File;
import com.conductor.core.model.org.OrganizationPrivilege;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.user.User;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.security.UserPrincipal;
import com.conductor.core.security.fiber.FiberIdentityProvider;
import com.conductor.core.security.fiber.FiberPermissionEvaluator;
import com.conductor.core.security.fiber.FiberPermissionEvaluatorChain;
import com.conductor.core.service.EventApplicationService;
import com.conductor.core.service.FileService;
import com.conductor.core.util.Pair;
import com.stripe.model.tax.Registration;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
public class EventApplicationController {

    private final EventApplicationService eventApplicationService;
    private final FileService fileService;
    private final FiberPermissionEvaluator permissionEvaluator;
    private final FiberIdentityProvider identityProvider;

    @PostMapping("/{event-id}/apply")
    public ResponseEntity<?> applyForEvent(
            @PathVariable("event-id")
            @NotBlank(message = "Event Id is required")
            @Size(min = 36, max = 36)
            String eventExternalId,
            @RequestBody FormResponse formResponse,
            Authentication authentication) {

         String applicationExternalId = eventApplicationService.apply(
                        (UserPrincipal)authentication.getPrincipal(),
                        eventExternalId,
                        formResponse.getFormResponse());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/applications/{application-id}/approve")
    public ResponseEntity<?> approveApplication(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String applicationExternalId,
            Authentication authentication) {

        Pair<String,String> parent = identityProvider.getApplicationParent(applicationExternalId);

         FiberPermissionEvaluatorChain.create(permissionEvaluator)
                .isOperator()
                .orPermission(parent.getLeft(), ResourceType.ORGANIZATION, Map.of(OrganizationPrivilege.EVENT, AccessLevel.WRITE))
                .orPermission(parent.getRight(), ResourceType.EVENT, Map.of(EventPrivilege.APPLICATION, AccessLevel.WRITE))
                .evaluate(authentication);
        //
        eventApplicationService.approveEventApplication(
                (UserPrincipal)authentication.getPrincipal(),
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
            Authentication authentication) {

        Pair<String,String> parent = identityProvider.getApplicationParent(applicationExternalId);
        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                .isOperator()
                .orPermission(parent.getLeft(), ResourceType.ORGANIZATION, Map.of(OrganizationPrivilege.EVENT, AccessLevel.WRITE))
                .orPermission(parent.getRight(), ResourceType.EVENT, Map.of(EventPrivilege.APPLICATION, AccessLevel.WRITE))
                .evaluate(authentication);

        eventApplicationService.rejectEventApplication(
                (UserPrincipal) authentication.getPrincipal(),
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

        if(permissionEvaluator.hasPermission(auth,applicationExternalId,ResourceType.APPLICATION,null)){
            throw new AccessDeniedException("You do not permissions to cancel this application");
        };

        eventApplicationService.cancelEventApplication((UserPrincipal) auth.getPrincipal(), applicationExternalId);
        return ResponseEntity.ok().build();
    }

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


        Pair<String,String> parent = identityProvider.getApplicationParent(applicationExternalId);

        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                //organization owner
                .orPermission(parent.getLeft(), ResourceType.ORGANIZATION, Map.of(OrganizationPrivilege.EVENT, AccessLevel.WRITE))
                //event level operator
                .orPermission(parent.getRight(), ResourceType.EVENT, Map.of(EventPrivilege.APPLICATION, AccessLevel.WRITE))
                //user whp submitted the application
                .orPermission(applicationExternalId, ResourceType.APPLICATION, null)
                .evaluate(authentication);

        eventApplicationService.comment(
                (UserPrincipal)authentication.getPrincipal(),
                applicationExternalId,
                comment
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{event-id}/applications")
    public ResponseEntity<?> getEventApplications(
            @Parameter(description = "External ID of the event", required = true)
            @PathVariable("event-id") @NotBlank String eventExternalId,
            Authentication authentication) {


        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                .orPermission(identityProvider.getRootResource(ResourceType.ORGANIZATION,eventExternalId), ResourceType.ORGANIZATION, Map.of(OrganizationPrivilege.EVENT, AccessLevel.WRITE))
                .orPermission(eventExternalId, ResourceType.EVENT, Map.of(EventPrivilege.APPLICATION, AccessLevel.WRITE))
                .evaluate(authentication);

        List<ApplicationDTO> applications = eventApplicationService.getEventApplications(eventExternalId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{event-id}/form")
    public ResponseEntity<?> getEventForm(
            @Parameter(description = "External ID of the event", required = true)
            @PathVariable("event-id") @NotBlank String eventExternalId) {

        String form = eventApplicationService.getEventForm(eventExternalId);

        return ResponseEntity.ok(Map.of("form",form));
    }

    @PostMapping("/{application-id}/files/")
    public ResponseEntity<?> uploadFile(
            @PathVariable("application-id")
            @NotBlank(message = "Application Id is required")
            @Size(min = 36, max = 36)
            String eventApplicationExternalId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        if(permissionEvaluator.hasPermission(authentication,eventApplicationExternalId,ResourceType.APPLICATION,null)){
            throw new AccessDeniedException("You do not permissions to upload files to this application");
        };

        eventApplicationService.storeFile((UserPrincipal) authentication.getPrincipal(), file,eventApplicationExternalId);
        return ResponseEntity.ok().build();
    }

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
        Pair<String,String> parent = identityProvider.getApplicationParent(applicationExternalId);

        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                //organization owner
                .orPermission(parent.getLeft(), ResourceType.ORGANIZATION, Map.of(OrganizationPrivilege.EVENT, AccessLevel.WRITE))
                //event level operator
                .orPermission(parent.getRight(), ResourceType.EVENT, Map.of(EventPrivilege.APPLICATION, AccessLevel.WRITE))
                //user whp submitted the application
                .orPermission(applicationExternalId, ResourceType.APPLICATION, null)
                .evaluate(auth);


        File file = fileService.getFile(fileExternalId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getData());
    }


}
