package com.conductor.core.service;

import com.conductor.core.dto.ApplicationDTO;
import com.conductor.core.exception.*;
import com.conductor.core.model.application.ApplicationComment;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.user.Operator;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.repository.*;
import com.conductor.core.util.ApplicationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.application.ApplicationStatus;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.ticket.Ticket;
import com.conductor.core.model.ticket.TicketStatus;
import com.conductor.core.model.user.User;
import com.stripe.model.tax.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for handling event applications/reservations.
 * Users can submit applications for events, and organization operators
 * can approve/reject them, resulting in ticket creation.
 */
@Service
@RequiredArgsConstructor
public class EventApplicationService {

    private final EventApplicationRepository eventApplicationRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final OperatorRepository operatorRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Enroll for an event via an application. If event accepts applications
     * a new application and user has paid (if payment is required) for the event,
     * a new application is created. Both the client and organization can communicate
     * via the application.
     *
     * @return external id of the application.
     */
    @Transactional
    public String apply(
            Authentication auth,
            String eventExternalId) {

        notNull(eventExternalId, "Event id is required");

        if(Objects.isNull(eventExternalId)) {
            throw new IllegalArgumentException("Event id is required");
        }
        User currentUser = (User) auth.getPrincipal();

        //check payment status

        Event event = eventRepository.findByExternalId(eventExternalId)
                .orElseThrow(
                        () -> new EventNotFoundException()
                );

        if(!event.requiresApplication()) {
            throw new ApplicationSubmissionFailed("This event does not accept applications");
        }
        if(!event.acceptsApplication()) {
            throw new ApplicationSubmissionFailed("This event is no longer accepting applications");
        }

        // Check if user already has a pending application for this event
        if (eventApplicationRepository.existsBySubmittedByAndTargetResource(
                currentUser, event)) {
            throw new ApplicationSubmissionFailed("You already have a pending application for this event");
        }

        Application application = Application.createNewApplicatonForEvent(event,(User) auth.getPrincipal());
        currentUser.getApplications().add(application);

        // To Do: asynchronously do analytics if configured

        userRepository.save(currentUser);
        
        return application.getExternalId();
    }

    @Transactional
    public void submitApplicationFormResponse(
            Authentication auth,
            String applicationExternalId,
            String formResponse) {

        notNull(applicationExternalId, "Application Id is required");
        notNull(formResponse, "Form response is required");

        Application application = findApplication(applicationExternalId);

        if (!application.hasForm()) {
            throw new FormNotFoundException("Form not configured for this application");
        }

        validateFormResponse(formResponse, "Invalid form response");

        application.setApplicationFormResponse(formResponse);
        applicationRepository.save(application);
    }

    @Transactional
    public void approveEventApplication(
            Authentication auth,
            String applicationExternalId)
    {

        notNull(applicationExternalId, "Application Id is required");

        User currentUser = (User) auth.getPrincipal();
        Application application = findApplication(applicationExternalId);

        application.approve(currentUser);
        applicationRepository.save(application);

        // Create ticket for the user who submitted the application
        Event event = (Event) application.getTargetResource();
        User user = application.getSubmittedBy();

        Ticket ticket = Ticket.creatNewTicket(user,event);

        user.getTickets().add(ticket);
        userRepository.save(user);

    }


    @Transactional
    public void cancelEventApplication(
            Authentication auth,
            String applicationExternalId)
    {
        notNull(applicationExternalId, "Application Id is required");

        User currentUser = (User)auth.getPrincipal();
        Application application = findApplication(applicationExternalId);

        application.cancel();
        applicationRepository.save(application);
    }


    @Transactional
    public void rejectEventApplication(
            Authentication auth,
            String applicationExternalId,
            String reason)
    {
        notNull(applicationExternalId, "Application Id is required");
        notNull(reason, "Rejection reason is required");

        Application application = findApplication(applicationExternalId);

        application.reject((User) auth.getPrincipal(), reason);
        applicationRepository.save(application);
    }


    private void validateFormResponse(String response, String errorMessage) {
        try {
            JsonNode node = objectMapper.readTree(response);
            if (node == null || node.isNull()) {
                throw new InvalidFormSubmissionException(errorMessage);
            }
        } catch (Exception e) {
            throw new InvalidFormSubmissionException(errorMessage);
        }
    }


    @Transactional
    public void addComment(
            Authentication auth,
            String applicationExternalId,
            String comment) {

        notNull(applicationExternalId, "Application Id is required");
        notNull(comment, "Comment is required");

        Application application = findApplication(applicationExternalId);

        application.addComment((User) auth.getPrincipal(), comment);
        applicationRepository.save(application);
    }

    public List<ApplicationDTO> getEventApplications(String eventExternalId) {
        Event event = eventRepository.findByExternalId(eventExternalId)
                .orElseThrow(
                        () -> new EventNotFoundException()
                );

        return applicationRepository
                .findByTargetResource(event).stream()
                .map(ApplicationMapper::toDto)
                .collect(Collectors.toList());
    }


    public List<ApplicationDTO> getUserEventApplications(Authentication auth) {
        return applicationRepository.findBySubmittedBy((User) auth.getPrincipal()).stream()
                .map(ApplicationMapper::toDto)
                .collect(Collectors.toList());
    }


    private Application findApplication(String applicationExternalId) {
        Optional<Application> appOpt = applicationRepository.findByExternalId(applicationExternalId);
        if (
                appOpt.isEmpty() ||
                !ResourceType.EVENT.equals(appOpt.get().getTargetResource().getResourceType()))
        {
            throw new ApplicationNotFound("Application not found");
        }
        return appOpt.get();
    }

    private void notNull(Object ob, String message)
    {
        if(Objects.isNull(ob)) {
            throw new IllegalArgumentException(message);
        }
    }

}
