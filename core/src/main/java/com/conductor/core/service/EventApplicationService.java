package com.conductor.core.service;

import com.conductor.core.dto.ApplicationDTO;
import com.conductor.core.exception.*;
import com.conductor.core.manager.ApplicationManager;
import com.conductor.core.model.ticket.Ticket;
import com.conductor.core.repository.*;
import com.conductor.core.util.ApplicationMapper;
import com.conductor.core.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for handling event applications (reservations).
 * <p>
 * This service provides methods for participants to apply to events,
 * submit application forms, and manage application lifecycle operations
 * (approve, reject, cancel, comment). It interacts with repositories
 * and delegates core business logic to {@link ApplicationManager}.
 * </p>
 *
 * <p>
 * Typical workflow:
 * <ul>
 *     <li>Users apply for an event that requires an application.</li>
 *     <li>Operators approve or reject submitted applications.</li>
 *     <li>Approved applications may result in ticket issuance.</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class EventApplicationService {

    private final ApplicationManager applicationManager;
    private final EventApplicationRepository eventApplicationRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final OperatorRepository operatorRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Apply for a given event.
     * <p>
     * This validates that the event exists and currently accepts applications.
     * Verifies form response and then finally registers a new application for
     * the user.
     * </p>
     *
     * @param eventExternalId external identifier of the event
     * @param formResponse response to the form associated with the event
     * @return external identifier of the newly created application
     *
     * @throws EventNotFoundException               if the event does not exist
     * @throws IllegalArgumentException             either arguments are {@code null or form response is in-valid
     * @throws ApplicationRequestFailedException    This exception can be thrown for the following resaons:-
     *  <p>
     *      <ul>
     *        <li>if an application for this event already exists.</li>
     *        <li>If the event does not accept or no longer accepts applications.</li>
     *        <li>If a database operations failed..</li>
     *      </ul>
     *  </p>
     */
    @Transactional
    public String apply(
            User user,
            String eventExternalId,
            String formResponse) {

        Event event = eventRepository.findByExternalId(eventExternalId)
                .orElseThrow(
                        () -> new EventNotFoundException()
                );

        if(!event.requiresApplication()) {
            throw new ApplicationRequestFailedException("This event does not accept applications");
        }
        if(!event.acceptsApplication()) {
            throw new ApplicationRequestFailedException("This event is no longer accepting applications");
        }

        if(!Objects.isNull(event.getApplicationForm())) {
            Utils.notNull(formResponse, "Response to the form is required");
        }
        // TODO: validate form response

        // TODO: check if the  current user has paid for this event

        Application application = applicationManager.registerApplication(
                user,
                event,
                formResponse
                );

        // TODO: asynchronously do analytics if configured

        return application.getExternalId();
    }

    /**
     * Get an event's form by its external identifier
     * @param eventExternalId external id of the target event
     * @return Form of the event in {@code String}
     *
     * @throws IllegalArgumentException     if argument is {@code null}
     * @throws EventNotFoundException      if the event is not found.
     */
    @Transactional
    public String getEventForm(String eventExternalId)
    {
        Utils.notNull(eventExternalId, "Event Id is required");

        Event event = eventRepository.findByExternalId(eventExternalId)
                .orElseThrow(
                        () -> new EventNotFoundException()
                );
        return event.getApplicationForm().getFormSchema();
    }

    /**
     * Approve an application for an event. This grants a ticket to the user
     * that submitted the application a ticket.
     *
     * @param applicationExternalId external identifier of the application
     *
     * @throws ApplicationNotFound                  if the application cannot be found
     * @throws IllegalArgumentException             if application external id is {@code null}
     * @throws ApplicationRequestFailedException    for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public void approveEventApplication(
            User approvedBy,
            String applicationExternalId) {

        Application application = applicationManager.findApplication(applicationExternalId);
        if(!application.getTargetResource().getResourceType().equals(ResourceType.EVENT)){
            throw new ApplicationNotFound();
        }
        User submittedBy = application.getSubmittedBy();
        applicationManager.approveApplication(approvedBy, applicationExternalId);

        Ticket ticket = Ticket.creatNewTicket(
                submittedBy,
                (Event)application.getTargetResource());

        submittedBy.getTickets().add(ticket);
        try{
            userRepository.save(submittedBy);
        }catch (Exception e) {
            throw new ApplicationRequestFailedException();
        }
    }

    /**
     * Cancel an application for an event.
     *
     * @param applicationExternalId external identifier of the application
     *
     * @throws ApplicationNotFound                      if the application cannot be found
     * @throws ApplicationRequestFailedException        for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public void cancelEventApplication(String applicationExternalId)
    {
      applicationManager.cancelEventApplication(applicationExternalId);
    }

    /**
     * Reject an application for an event with a reason.
     *
     * @param applicationExternalId external identifier of the application
     * @param reason                reason for rejection
     *
     * @throws ApplicationNotFound                      if the application cannot be found
     * @throws ApplicationRequestFailedException        for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public void rejectEventApplication(
            User user,
            String applicationExternalId,
            String reason)
    {
        applicationManager.rejectEventApplication(
                user,
                applicationExternalId,
                reason);
    }

    /**
     * Add a comment to an event application.
     *
     * @param applicationExternalId external identifier of the application
     * @param comment               content of the comment
     *
     * @throws ApplicationNotFound                  if the application cannot be found
     * @throws ApplicationRequestFailedException    for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public void comment(
            User user,
            String applicationExternalId,
            String comment) {
       applicationManager.addComment(user,applicationExternalId,comment);
    }

    /**
     * Get all applications submitted for a given event.
     *
     * @param eventExternalId external identifier of the event
     * @return list of {@link ApplicationDTO} representing all event applications
     *
     * @throws ApplicationRequestFailedException    for all exceptions cause due to database operations failure.
     */
    @Transactional
    public List<ApplicationDTO> getEventApplications(String eventExternalId) {

        return applicationManager.getAllApplicationsForAResource(eventExternalId)
                .stream()
                .map(ApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    private final FileService fileService;

    @Transactional
    public void storeFile(MultipartFile file, String eventApplicationExternalId, User user) {
        User uploadedBy = userRepository.findById(user.getId()).orElseThrow(()->new RuntimeException("User not found"));
        Application application = applicationManager.findApplication(eventApplicationExternalId);
        fileService.storeFile(file, Optional.of(application), uploadedBy);
    }
}
