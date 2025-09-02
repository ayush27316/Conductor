package com.conductor.core.controller;

import com.conductor.core.dto.BookTicketRequest;
import com.conductor.core.dto.TicketDTO;
import com.conductor.core.exception.EventNotFoundException;
import com.conductor.core.service.TicketService;
import com.conductor.core.service.TicketPurchaseService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private final TicketPurchaseService ticketPurchaseService;

    @PostMapping("/book")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> bookTicketByRequest(
            @RequestBody BookTicketRequest request) {
        try{
            TicketDTO ticketDTO = ticketService.bookTicket(request.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(ticketDTO);
        }catch (EventNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/buy/{eventExternalId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> buyTicket(@PathVariable String eventExternalId,
                                            @RequestParam(value = "tags", required = false) String tagsCsv) {
        try {
            TicketDTO dto = ticketPurchaseService.buyTicketByEventExternalId(eventExternalId, tagsCsv);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Purchase failed");
        }
    }

    @GetMapping("/{ticketExternalId}/qr")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<byte[]> getTicketQr(@PathVariable String ticketExternalId) {
        try {
            byte[] png = ticketPurchaseService.getTicketQrPng(ticketExternalId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(png, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{ticketExternalId}/check-in")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> checkIn(@PathVariable String ticketExternalId) {
        try {
            TicketDTO dto = ticketPurchaseService.checkInTicket(ticketExternalId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check-in failed");
        }
    }
}