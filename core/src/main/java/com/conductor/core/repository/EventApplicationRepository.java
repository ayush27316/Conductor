package com.conductor.core.repository;

import com.conductor.core.model.application.Application;
import com.conductor.core.model.application.ApplicationStatus;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Find event applications by event
     */
    List<Application> findByTargetResourceAndTargetResource_ResourceType(Event event, ResourceType resourceType);
    
    /**
     * Find event applications by user
     */
    List<Application> findBySubmittedByAndTargetResource_ResourceType(User user, ResourceType resourceType);
    
    /**
     * Find event applications by status
     */
    List<Application> findByApplicationStatusAndTargetResource_ResourceType(ApplicationStatus status, ResourceType resourceType);
    
    /**
     * Find event applications by event and status
     */
    List<Application> findByTargetResourceAndApplicationStatusAndTargetResource_ResourceType(Event event, ApplicationStatus status, ResourceType resourceType);
    
    /**
     * Find event applications by user and event
     */
    Optional<Application> findBySubmittedByAndTargetResourceAndTargetResource_ResourceType(User user, Event event, ResourceType resourceType);
    
    /**
     * Check if user has pending application for an event
     */
    boolean existsBySubmittedByAndTargetResource(User user, Event event);
}
