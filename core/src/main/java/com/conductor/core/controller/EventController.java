package com.conductor.core.controller;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.dto.EventRegistrationRequest;
import com.conductor.core.dto.ResponseDTO;
import com.conductor.core.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    //OPERATOR, 'ORGANIZATION', null, {EVENT:'write'})
    @PreAuthorize("hasRole('OPERATOR')")
    @PostMapping("/register")
    public ResponseDTO<?> registerEvent(
            @Valid @RequestBody EventRegistrationRequest request) {

        eventService.registerEvent(request);
        return ResponseDTO.success("");
    }


    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents(){
        return ResponseEntity.ok(eventService.getAllEvents());
    }

//    @GetMapping
//    public ResponseEntity<List<Event>> getEventsByOrganizationName(
//            @RequestParam String organizationName) {
//        List<Event> events = eventService.getEventsByOrganizationName(organizationName);
//        return ResponseEntity.ok(events);
//    }
//
//    @PostMapping("/operators")
//    public ResponseEntity<Operator> addOperator(@RequestParam String organizationName,
//                                                @RequestParam String eventName,
//                                                @RequestBody Operator operator) {
//        Operator savedOperator = eventService.addOperatorToEvent(organizationName, eventName, operator);
//        return ResponseEntity.ok(savedOperator);
//    }
}
