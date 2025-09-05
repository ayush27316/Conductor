package com.conductor.core.repository;

import com.conductor.core.model.event.Event;
import com.conductor.core.model.ticket.Ticket;
import com.conductor.core.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    public Optional<Ticket> findByUserAndEvent(User user, Event event);

    Optional<Ticket> findByExternalId(String externalId);
}
