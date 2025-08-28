package com.conductor.core.service;

import com.conductor.core.dto.TicketDTO;
import com.conductor.core.exception.EventNotFoundException;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.ticket.Ticket;
import com.conductor.core.model.ticket.TicketStatus;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.EventRepository;
import com.conductor.core.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public TicketDTO bookTicket(String name){
        Optional<Event> events = eventRepository.findByName(name);

        if(events.isEmpty()){
            throw new EventNotFoundException("Event not found");
        }

        Event event = events.get();

        User user = getCurrentUser();

        Ticket ticket = Ticket.builder()
                .event(event)
                .user(user)
                .status(TicketStatus.IDLE)
                .build();
        ticketRepository.save(ticket);

        return TicketDTO.builder()
                .eventExternalId(event.getExternalId())
                .ownerUsername(user.getUsername())
                .code(ticket.getExternalId()).
                build();
    }

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Invalid authentication context");
        }

        return  (User) authentication.getPrincipal();
    }
}
