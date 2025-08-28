package com.conductor.core.controller;

import com.conductor.core.dto.BookTicketRequest;
import com.conductor.core.dto.ResponseDTO;
import com.conductor.core.dto.TicketDTO;
import com.conductor.core.exception.EventNotFoundException;
import com.conductor.core.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/book")
    @PreAuthorize("hasRole('USER')")
    public ResponseDTO bookTicketByRequest(
            @RequestBody BookTicketRequest request) {
    try{
        TicketDTO ticketDTO = ticketService.bookTicket(request.getName());
        return ResponseDTO.success("Ticket booked", (Object) ticketDTO);
    }catch (EventNotFoundException e)
    {
        return ResponseDTO.internalServerError(e.getMessage());
    }
    }

}