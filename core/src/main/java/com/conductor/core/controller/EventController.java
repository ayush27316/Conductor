package com.conductor.core.controller;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.dto.ResponseDTO;
import com.conductor.core.exception.EventRegistrationFailedException;
import com.conductor.core.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    //OPERATOR, 'ORGANIZATION', null, {EVENT:'write'})
    @PreAuthorize("hasRole('OPERATOR')")
    @PostMapping("/register")
    public ResponseDTO<?> registerEvent(
            @Valid @RequestBody EventDTO request) {

        try {
            Boolean result = eventService.registerEvent(request);
            if(result){

                return ResponseDTO.success("Event registration successful.");
            }else {
                throw new EventRegistrationFailedException("Event registration failed due to an internal server error");
            }

        }catch (EventRegistrationFailedException e){
            return ResponseDTO.internalServerError(e.getMessage());
        }catch (RuntimeException e){
            return ResponseDTO.builder()
                    .status(500)
                    .success(false)
                    .message("Registration failed due to an internal error")
                    .description(e.getMessage())
                    .timeStamp(LocalDateTime.now().toString())
                    .build();
        }
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseDTO<?> getAllEvents(){

        try{
            List<EventDTO> eventDTOList = eventService.getAllEvents();

            return ResponseDTO.success(null, (Object)eventDTOList );
        }catch (RuntimeException e){
            return ResponseDTO.internalServerError(e.getMessage());
        }

    }
}
