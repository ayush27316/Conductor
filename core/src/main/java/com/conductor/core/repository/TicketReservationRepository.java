package com.conductor.core.repository;

import com.conductor.core.model.ticket.TicketReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketReservationRepository extends JpaRepository<TicketReservation, Long> {

    public Optional<TicketReservation> findByExternalId(String externalId);

    @Query("SELECT t FROM TicketReservation t WHERE t.event.externalId = :eventExternalId AND t.status = 'PENDING'")
    List<TicketReservation> findAllPendingByEventExternalId(@Param("eventExternalId") String eventExternalId);
}
