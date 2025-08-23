package com.conductor.core.service;

import com.conductor.core.dto.OrganizationDTO;
import com.conductor.core.util.OrganizationMapper;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.org.OrganizationAudit;
import com.conductor.core.model.ticket.TicketReservation;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);
    private static final String ORGANIZATION_ONBOARDING_EVENT = "organization_onboarding";
    private static final String CONDUCTOR_ORG_NAME = "conductor";

    @Autowired
    private OrganizationMapper organizationMapper ;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private OrganizationAuditRepository auditRepository;
    @Autowired
    private TicketReservationRepository ticketReservationRepository;
    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private UserRepository userRepository;

    ObjectMapper mapper = new ObjectMapper();

    /*registerOrganization is the first step before an organization can
    * create events. Internally this creates a new reservation for conductor
    * organized event "organization_onboarding". Once operators from conductor
    * org approves the reservation organization onboarding is initiated
    * */
    @Transactional
    public void registerOrganization(OrganizationDTO dto){
        Organization organization = organizationRepository.findByName("conductor")
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        Event event = eventRepository.findByOrganizationIdAndShortName(organization.getId(), "organization_onboarding")
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        try{
            User demoUser = userRepository.findByUsername("demo-user") .orElseThrow(() ->
                    new UsernameNotFoundException("User not found with")
            );
            String json = mapper.writeValueAsString(dto);
            TicketReservation reservation = TicketReservation.builder()
                    .event(event)
                    .status(TicketReservation.Status.PENDING)
                    .user(demoUser)//replace User from context
                    .metadata(json).build();
            ticketReservationRepository.save(reservation);
        }catch (Exception e){};
    }

    /**
     * Approve reservation and initiate organization onboarding
     * Returns true if successful, false if reservation is not for organization onboarding
     */
    @Transactional
    public boolean approveOrganization(String reservationExternalId) {
        logger.info("Starting organization approval for reservation: {}", reservationExternalId);


        try {
            TicketReservation reservation = ticketReservationRepository.findByExternalId(reservationExternalId)
                    .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + reservationExternalId));

            // Check if reservation is for organization onboarding
            if (!Objects.equals(reservation.getEvent().getShortName(), ORGANIZATION_ONBOARDING_EVENT)) {
                logger.warn("Reservation {} is not for organization onboarding. Event: {}",
                        reservationExternalId, reservation.getEvent().getShortName());
                return false;
            }

            // Check if already approved
            if (reservation.getStatus() == TicketReservation.Status.APPROVED) {
                logger.warn("Reservation {} is already approved", reservationExternalId);
                throw new IllegalStateException("Reservation is already approved");
            }

            // Update reservation status
            reservation.setStatus(TicketReservation.Status.APPROVED);
            ticketReservationRepository.save(reservation);

            OrganizationDTO dtoFromJson = parseOrganizationFromMetadata(reservation.getMetadata());
            initiateOrganizationOnboarding(dtoFromJson);

            logger.info("Organization approval completed successfully for reservation: {}", reservationExternalId);
            return true;

        } catch (DataAccessException e) {
            logger.error("Database error during organization approval for reservation: {}", reservationExternalId, e);
            throw new RuntimeException("Database error occurred during approval", e);
        }
    }

    /**
     * Organization has been approved. Create organization from dto,
     * add the initial audit report and create an operator with OWNER
     * level access.
     */
    @Transactional
    public void initiateOrganizationOnboarding(OrganizationDTO dto) {
        logger.info("Initiating organization onboarding for: {}", dto.getName());

        try {
            // Check if organization already exists
            if (organizationRepository.findByName(dto.getName()).isPresent()) {
                throw new IllegalStateException("Organization with name '" + dto.getName() + "' already exists");
            }

            // Create organization
            Organization org = organizationMapper.toEntity(dto);
            OrganizationAudit audit = OrganizationAudit.getBlankAudit(org);
            org.setAudit(audit);
            org = organizationRepository.save(org);

            // Create owner user and operator
            User user = User.builder()
                    .type(User.UserType.OPERATOR)
                    .username((dto.getName()))
                    .password((dto.getName()))
                    .emailAddress(org.getEmail())
                    .build();

            userRepository.save(user);

            Operator operator = Operator.builder()
                    .user(user)
                    .role(Role.OWNER)
                    .organization(org)
                    .build();

            operatorRepository.save(operator);

            logger.info("Organization onboarding completed successfully for: {}", dto.getName());

            // TODO: Implement async call to email service to send credentials

        } catch (DataAccessException e) {
            logger.error("Database error during organization onboarding for: {}", dto.getName(), e);
            throw new RuntimeException("Database error occurred during onboarding", e);
        }
    }

    /**
     * Parse organization DTO from JSON metadata
     */
    private OrganizationDTO parseOrganizationFromMetadata(String metadata) {
        try {
            return mapper.readValue(metadata, OrganizationDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse organization DTO from metadata: {}", metadata, e);
            throw new RuntimeException("Failed to parse organization data from reservation", e);
        }
    }
    /**
     * Get all organizations waiting for approval
     */
    public List<TicketReservation> getAllOrganizationsWaitingForApproval() {
        logger.info("Fetching all organizations waiting for approval");
        try {
            List<TicketReservation> reservations =  ticketReservationRepository.findAllPendingByEventExternalId(ORGANIZATION_ONBOARDING_EVENT);
            return reservations;
        } catch (DataAccessException e) {
            logger.error("Database error while fetching pending organizations", e);
            throw new RuntimeException("Failed to fetch pending organizations", e);
        }
    }

}
