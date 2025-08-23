package com.conductor.core.controler;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<String> registerEvent(
            @RequestParam String organizationName,
            @RequestBody EventDTO eventDTO) {
        eventService.registerEvent(eventDTO, organizationName);
        return ResponseEntity.ok("Event registered successfully");
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
