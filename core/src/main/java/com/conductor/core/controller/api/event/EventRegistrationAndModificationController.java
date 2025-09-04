package com.conductor.core.controller.api.event;

import com.conductor.core.dto.event.EventRegistrationRequest;
import com.conductor.core.exception.EventRegistrationFailedException;
import com.conductor.core.model.user.User;
import com.conductor.core.service.EventRegistrationAndModificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventRegistrationAndModificationController {

    private final EventRegistrationAndModificationService eventService;

    @PreAuthorize("hasPermission(#request.organizationId, 'organization', {'event': 'write'})")
    @PostMapping("/register")
    public ResponseEntity<?> registerEvent(
            Authentication auth,
            @Valid @RequestBody EventRegistrationRequest request) {

        try {
            Boolean result = eventService.registerEvent((User) auth.getPrincipal(),request);
            if(result){
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }else {
                throw new EventRegistrationFailedException("Event registration failed due to an internal server error");
            }

        }catch (EventRegistrationFailedException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed due to an internal error");
        }
    }

//
//    @PreAuthorize("hasRole('USER')")
//    @GetMapping
//    public ResponseEntity<List<EventDTO>> getAllEvents(){
//
//        try{
//            List<EventDTO> eventDTOList = eventService.getAllEvents();
//            return ResponseEntity.ok(eventDTOList);
//        }catch (RuntimeException e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//
//    }
}
