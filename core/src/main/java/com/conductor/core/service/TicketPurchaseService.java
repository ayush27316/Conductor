package com.conductor.core.service;

import com.conductor.core.dto.TicketDTO;
import com.conductor.core.exception.EventNotFoundException;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.event.EventOption;
import com.conductor.core.model.ticket.Ticket;
import com.conductor.core.model.ticket.TicketStatus;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.EventRepository;
import com.conductor.core.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketPurchaseService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;

    @Transactional
    public TicketDTO buyTicketByEventExternalId(String eventExternalId, String tagsCsv) {
        Event event = eventRepository.findByExternalId(eventExternalId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        // If REQUIRES_APPROVAL present, disallow direct purchase
        if (event.getOptions() != null && event.getOptions().contains(EventOption.REQUIRES_APPROVAL)) {
            throw new IllegalStateException("This event requires approval. Please submit an application.");
        }

        User user = getCurrentUser();

        Ticket ticket = Ticket.builder()
                .event(event)
                .user(user)
                .status(TicketStatus.IDLE)
                .tags(tagsCsv)
                .build();
        ticketRepository.save(ticket);

        return TicketDTO.builder()
                .eventExternalId(event.getExternalId())
                .ownerUsername(user.getUsername())
                .code(ticket.getExternalId())
                .build();
    }

    public byte[] getTicketQrPng(String ticketExternalId) {
        Ticket ticket = ticketRepository.findByExternalId(ticketExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        return qrCodeService.generatePngFromText(ticket.getExternalId());
    }

    @Transactional
    public TicketDTO checkInTicket(String ticketExternalId) {
        Ticket ticket = ticketRepository.findByExternalId(ticketExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        // Mark checked in
        ticket.setStatus(TicketStatus.CHECKED_IN);
        ticket.setCheckedInAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        return TicketDTO.builder()
                .eventExternalId(ticket.getEvent().getExternalId())
                .ownerUsername(ticket.getUser().getUsername())
                .code(ticket.getExternalId())
                .build();
    }

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Invalid authentication context");
        }
        return  (User) authentication.getPrincipal();
    }
}
