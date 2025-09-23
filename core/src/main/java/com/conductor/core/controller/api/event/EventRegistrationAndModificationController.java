package com.conductor.core.controller.api.event;

import com.conductor.core.dto.event.EventModification;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.org.OrganizationPrivilege;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.security.UserPrincipal;
import com.conductor.core.security.fiber.FiberIdentityProvider;
import com.conductor.core.security.fiber.FiberPermissionEvaluator;
import org.springframework.web.bind.annotation.*;
import com.conductor.core.security.fiber.FiberPermissionEvaluatorChain;
import com.conductor.core.service.EventRegistrationAndModificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventRegistrationAndModificationController {

    private final EventRegistrationAndModificationService eventService;
    private final FiberPermissionEvaluator permissionEvaluator;
    private final FiberIdentityProvider identityProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerEvent(
            Authentication auth,
            @Valid @RequestBody EventModification request) {

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        permissionEvaluator.hasPermission(
                auth,
                principal.getOrganizationExternalId(),
                ResourceType.ORGANIZATION,
                Map.of(OrganizationPrivilege.EVENT, AccessLevel.WRITE));

//        FiberPermissionEvaluatorChain.create(permissionEvaluator)
//                .addRole(UserRole.OPERATOR)
//                .addPermission(
//                        principal.getOrganizationExternalId(),
//                        ResourceType.ORGANIZATION,
//                        Map.of(OrganizationPrivilege.EVENT, AccessLevel.WRITE) )
//                .evaluate(auth);

        String applicationId = eventService.registerEvent(principal.getOrganizationExternalId(),request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("event-id", applicationId));
    }

    @PostMapping("/{event-id}/modify")
    public ResponseEntity<?> modifyEvent(
            @PathVariable("event-id")
            @NotBlank(message = "Event Id is required")
            @Size(min = 36, max = 36)
            String eventExternalId,
            @Valid @RequestBody EventModification request,
            Authentication auth)
    {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        FiberPermissionEvaluatorChain permissionSubChain = FiberPermissionEvaluatorChain.create(permissionEvaluator)
                .orPermission(
                        identityProvider.getRootResource(ResourceType.EVENT, eventExternalId),
                        ResourceType.ORGANIZATION,
                        Map.of(OrganizationPrivilege.EVENT, AccessLevel.WRITE) )
                .orPermission(
                        eventExternalId,
                        ResourceType.EVENT,
                        Map.of(OrganizationPrivilege.CONFIG, AccessLevel.WRITE) );

        FiberPermissionEvaluatorChain.create(permissionEvaluator)
                .addRole(UserRole.OPERATOR)
                .addGroup(permissionSubChain)
                .evaluate(auth);

        eventService.modifyEvent(eventExternalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
