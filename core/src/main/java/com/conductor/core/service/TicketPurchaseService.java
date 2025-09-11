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
import com.conductor.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Acquiring a ticket for an event requires multiple steps based on different event configuration.
 * if an Event has a form associated with it then user must first fill the form, then if it requires
 * payment they must make the payment, if approval is required then an application is created and
 * ticket is on hold until the application is approved. Then emailing the reciept of the payment
 * and confirmation for enrolling in the event with the ticket.
 *
 * Among all two important thinfs to maintin is idempotency, and that the number of tickets
 * distributed is never more than what was configured at the time event was created.
 *
 */
@Service
@RequiredArgsConstructor
public class TicketPurchaseService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;
    private final UserRepository userRepository;

    @Transactional
    public List<TicketDTO> getAllTickets() {
        User user = getCurrentUser();

        user = userRepository.findByExternalId(user.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("Username not found")
        );

        List<Ticket> tickets = user.getTickets();

        return
                tickets.stream()
                        .map(ticket ->
                            TicketDTO.builder()
                            .code(ticket.getExternalId())
                            .eventExternalId(ticket.getEvent().getExternalId())
                                    .username(ticket.getUser().getUsername())
                            .build()
                        )
                        .toList();
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
                .username(ticket.getUser().getUsername())
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
