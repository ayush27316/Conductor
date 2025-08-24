package com.conductor.core.repository;

import com.conductor.core.model.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrganizationId(Long organizationId);
    Optional<Event> findByOrganizationIdAndShortName(Long organizationId, String shortName);

    Optional<Event> findByExternalId(String externalId);
}
