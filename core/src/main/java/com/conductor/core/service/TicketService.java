package com.conductor.core.service;

import com.conductor.core.dto.TicketDTO;
import com.conductor.core.model.ticket.Ticket;
import com.conductor.core.model.ticket.TicketStatus;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.EventRepository;
import com.conductor.core.repository.TicketRepository;
import com.conductor.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;
    private final UserRepository userRepository;

    @Transactional
    public List<TicketDTO> getAllTickets() {
        User user = getCurrentUser();

        user = userRepository.findByExternalId(user.getExternalId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        List<Ticket> tickets = user.getTickets();

        final User finalUser = user;
        return
                tickets.stream()
                        .map(ticket ->
                            TicketDTO.builder()
                                    .username(finalUser.getUsername())
                                    .code(ticket.getExternalId())
                                    .eventExternalId(ticket.getEvent().getExternalId())
                            .build()
                        )
                        .toList();
    }

    //code for tickets must be signed token from events and that we cache secrets of events for faster validation
    //and checkins
    @Transactional
    public boolean validate(String ticketExternalId, String eventExternalId)
    {
        //the way we are building permissiosn it might be the case the
        //case that we don't need special validation here
        return true;
    }

    @Transactional
    public void checkInTicket(String ticketExternalId) {
        Ticket ticket = ticketRepository.findByExternalId(ticketExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        // Mark checked in
        ticket.setStatus(TicketStatus.CHECKED_IN);
        ticket.setCheckedInAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Invalid authentication context");
        }
        return  (User) authentication.getPrincipal();
    }
}
