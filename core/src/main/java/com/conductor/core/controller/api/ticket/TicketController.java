package com.conductor.core.controller.api.ticket;

import com.conductor.core.dto.TicketValidationRequest;
import com.conductor.core.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TicketController {

   private final TicketService ticketService;


   @PostMapping("/validate")
   //@PreAuthorize("hasRole('OPERATOR')")
   public ResponseEntity<?> validate(
           @RequestBody TicketValidationRequest request
   ) {

       ticketService.validate(request.getTicketExternalId(), request.getEventExternalId());
       return ResponseEntity.status(HttpStatus.CREATED).build();
   }


   //we shoudl have two options onLine checkin via email
    //and offline checkin
   @PostMapping("/{ticket-id}/check-in")
   //@PreAuthorize("hasRole('OPERATOR')")
   public ResponseEntity<?> checkIn(
           @PathVariable(name = "ticket-id") String ticketExternalId) {
           ticketService.checkInTicket(ticketExternalId);
           return ResponseEntity.ok().build();
   }
}