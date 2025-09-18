package com.conductor.core.controller.api.event;

import com.conductor.core.dto.event.EventModification;
import com.conductor.core.model.user.User;
import com.conductor.core.security.fiber.FiberPermissionEvaluator;
import com.conductor.core.service.EventRegistrationAndModificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventRegistrationAndModificationController {

    private final EventRegistrationAndModificationService eventService;
    private final FiberPermissionEvaluator fiberPermissionEvaluator;

    @PreAuthorize("hasPermission(#request.organizationId, 'organization', {'event': 'write'})")
    @PostMapping("/register")
    public ResponseEntity<?> registerEvent(
            Authentication auth,
            @Valid @RequestBody EventModification request) {

        //the user is an operator for an organizaiton and that within its organizaiton
        //he has access toe event
        //fiberPermissionEvaluator.hasPermission(auth,null, );

        String applicationId = eventService.registerEvent((User) auth.getPrincipal(),request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("event-id", applicationId));
    }

    //@PreAuthorize()
    @PostMapping("/{event-id}/modify")
    public ResponseEntity<?> modifyEvent(
            @PathVariable("event-id")
            @NotBlank(message = "Event Id is required")
            @Size(min = 36, max = 36)
            String eventExternalId,
            @Valid @RequestBody EventModification request,
            Authentication auth)
    {
        eventService.modifyEvent((User) auth.getPrincipal(), eventExternalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
