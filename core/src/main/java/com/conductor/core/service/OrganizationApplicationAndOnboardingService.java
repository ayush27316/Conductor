package com.conductor.core.service;

import com.conductor.core.dto.OrganizationApplicationRequest;
import com.conductor.core.exception.ApplicationNotFound;
import com.conductor.core.exception.ApplicationRequestFailedException;
import com.conductor.core.manager.ApplicationManager;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.application.ApplicationStatus;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.user.Operator;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.org.OrganizationAudit;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * Service for managing organization applications and initiating the onboarding process.
 *
 * {@link Organization} onboarding starts with creating an {@link Application}
 * for it. Admin users can then interact with the user that made the request using
 * this Application. This service handles the complete lifecycle of organization
 * applications including creation, approval, rejection, cancellation, and commenting.
 *
 * @see Organization
 * @see Application
 * @see ApplicationManager
 */
@Service
@RequiredArgsConstructor
public class OrganizationApplicationAndOnboardingService {

    private final ApplicationRepository applicationRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationAuditRepository auditRepository;
    private final UserRepository userRepository;
    private final ApplicationManager applicationManager;
    private final OperatorRepository operatorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PermissionRepository permissionRepository;

    /**
     * Creates a new organization application for the specified user.
     *
     * This method performs the following operations:
     * <ul>
     *   <li>Validates that the organization name is not already taken</li>
     *   <li>Creates a new {@link Organization} entity with the provided details</li>
     *   <li>Persists the organization to the database</li>
     *   <li>Creates an {@link Application} for the organization</li>
     *   <li>Grants the submitting user permission to access the application</li>
     * </ul>
     *
     * @param submittedBy the {@link User} submitting the organization application (must not be {@code null})
     * @param request the {@link OrganizationApplicationRequest} containing organization details (must not be {@code null})
     *
     * @return the external ID of the created application as a {@link String}
     *
     * @throws RuntimeException if the organization name is already taken
     * @throws RuntimeException if organization registration fails due to database constraints
     * @throws ApplicationRequestFailedException if application creation fails
     * @throws JsonProcessingException if serialization of the request object fails
     * @throws RuntimeException if JSON processing fails during application creation
     *
     * @see OrganizationApplicationRequest
     */
    @Transactional
    public String apply(
            User submittedBy,
            OrganizationApplicationRequest request) {

        //check if organization name already exist
        if (!organizationRepository.findByName(request.getName()).isEmpty()) {
            throw new RuntimeException("Organization name already taken");
        }

        // TODO: validate email

        Organization organization = Organization.builder()
                .name(request.getName())
                .email(request.getEmail())
                .description(request.getDescription())
                .locations(request.getLocations())
                .websiteUrl(request.getWebsiteUrl())
                .tags(request.getTags())
                .build();
        try {
            organizationRepository.save(organization);
        } catch (Exception e) {
            throw new RuntimeException("Organization registration failed." + e.getMessage(), e);
        }

        Application application = null;

        try {
            application = applicationManager.registerApplication(
                    submittedBy,
                    organization,
                    objectMapper.writeValueAsString(request)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //grant user necessary permission to access the new application
        Permission permission =Permission.builder()
                            .resource(application)
                            .grantedTo(submittedBy)
                    .build();
        permissionRepository.save(permission);

        return application.getExternalId();
    }

    /**
     * Approves an organization application and initiates the onboarding process.
     *
     * This method performs the following operations:
     * <ul>
     *   <li>Approves the application using {@link ApplicationManager}</li>
     *   <li>Retrieves the target organization from the application</li>
     *   <li>Initiates the organization onboarding process</li>
     * </ul>
     *
     * The entire operation is performed within a single transaction that will
     * rollback on any exception.
     *
     * @param approvedBy the {@link User} approving the application (must not be {@code null})
     * @param applicationExternalId the external ID of the application to approve (must not be {@code null})
     *
     * @throws ApplicationNotFound if the application with the given ID cannot be found
     * @throws IllegalArgumentException if any argument is {@code null}
     * @throws IllegalStateException if the application is already in a final state
     * @throws ApplicationRequestFailedException if the approval operation fails
     * @throws RuntimeException if the onboarding process fails
     *
     * @see ApplicationManager#approveApplication(User, String)
     * @see #initiateOrganizationOnboarding(Organization)
     */
    @Transactional(rollbackFor = Exception.class)
    public void approve(
            User approvedBy,
            String applicationExternalId) {

        Application application =
                applicationManager.approveApplication(approvedBy, applicationExternalId);

        Resource target = application.getTargetResource();
        initiateOrganizationOnboarding(Resource.safeCast(Organization.class,target).get());
    }

    /**
     * Rejects an organization application with a specified reason.
     *
     * The rejection reason will be added as a comment to the application.
     * The entire operation is performed within a single transaction that will
     * rollback on any exception.
     *
     * @param rejectedBy the {@link User} rejecting the application (must not be {@code null})
     * @param applicationExternalId the external ID of the application to reject (must not be {@code null})
     * @param rejectionReason the reason for rejection (must not be {@code null})
     *
     * @throws ApplicationNotFound if the application with the given ID cannot be found
     * @throws IllegalArgumentException if any argument is {@code null}
     * @throws IllegalStateException if the application is already in a final state
     * @throws ApplicationRequestFailedException if the rejection operation fails
     *
     * @see ApplicationManager#rejectEventApplication(User, String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    public void reject(
            User rejectedBy,
            String applicationExternalId,
            String rejectionReason) {

        applicationManager.rejectEventApplication(
                rejectedBy,
                applicationExternalId,
                rejectionReason);
    }

    /**
     * Cancels an organization application.
     *
     * This operation should typically be performed by the user who originally
     * submitted the application. The cancellation sets the application status
     * to {@link ApplicationStatus#CANCELLED}.
     *
     * @param applicationExternalId the external ID of the application to cancel (must not be {@code null})
     * @param cancelledBy the {@link User} cancelling the application (must not be {@code null})
     *
     * @throws ApplicationNotFound if the application with the given ID cannot be found
     * @throws IllegalStateException if the application is already in a final state
     * @throws ApplicationRequestFailedException if the cancellation operation fails
     * @throws SecurityException if the user doesn't have permission to cancel the application
     *
     * @see ApplicationManager#cancelEventApplication(String, User)
     */
    @Transactional
    public void cancel(String applicationExternalId, User cancelledBy) {
        applicationManager.cancelEventApplication(applicationExternalId, cancelledBy);
    }

    /**
     * Adds a comment to an organization application.
     *
     * Comments can be used for communication between the applicant and reviewers
     * during the application review process.
     *
     * @param user the {@link User} adding the comment (must not be {@code null})
     * @param applicationExternalId the external ID of the application (must not be {@code null})
     * @param comment the comment text to add (must not be {@code null})
     *
     * @throws ApplicationNotFound if the application with the given ID cannot be found
     * @throws IllegalArgumentException if any argument is {@code null}
     * @throws ApplicationRequestFailedException if the comment addition fails
     *
     * @see ApplicationManager#addComment(User, String, String)
     */
    @Transactional
    public void comment(
            User user,
            String applicationExternalId,
            String comment) {
        applicationManager.addComment(user,applicationExternalId,comment);
    }


    /**
     * Initiates the onboarding process for an approved organization.
     *
     * This private method performs the complete onboarding setup:
     * <ul>
     *   <li>Creates a blank audit record for the organization</li>
     *   <li>Sets up owner permissions for the organization</li>
     *   <li>Creates a default operator user account with generated credentials</li>
     *   <li>Associates the operator user with the organization</li>
     *   <li>Persists all entities to the database</li>
     * </ul>
     *
     * The entire operation is performed within a single transaction that will
     * rollback on any exception.
     *
     * @param org the {@link Organization} to onboard (must not be {@code null})
     *
     * @throws RuntimeException if any part of the onboarding process fails
     * @throws  DataAccessException if database operations fail
     *
     * @see OrganizationAudit#getBlankAudit(Organization)
     * @see Organization#getOwnerPermission()
     */
    @Transactional(rollbackFor = Exception.class)
    private void initiateOrganizationOnboarding(Organization org){
        OrganizationAudit audit = OrganizationAudit.getBlankAudit(org);

        Permission permission = Permission.builder()
                .resource(org)
                .permission(Organization.getOwnerPermission()).build();

        organizationRepository.save(org);
        auditRepository.save(audit);

        User user = User.builder()
                .role(UserRole.OPERATOR)
                .username(org.getName())
                .password(passwordEncoder.encode(org.getName()+"00xx"))
                .firstName(org.getName())
                .lastName(org.getName())
                .emailAddress(org.getEmail())
                .build();


        permission.setGrantedTo(user);
        user.setPermissions(List.of(permission));

        userRepository.save(user);

        Operator operator = Operator.builder()
                .user(user)
                .organization(org)
                .build();
        operatorRepository.save(operator);

        // TODO: Implement async call to email service to send credentials
        // transactions should be maintained if emailing fails
    }
    /**
     * Retrieves all organization applications that are waiting for approval.
     *
     * This method returns all applications with {@link ApplicationStatus#PENDING}
     * status for organizations that require administrative review and approval.
     *
     * @return a {@link List} of {@link Application} objects waiting for approval,
     *         or {@code null} in the current incomplete implementation
     *
     * @throws ApplicationRequestFailedException if the database operation fails
     *
     * @see ApplicationManager#getAllPendingApplicationsOfResourceType(ResourceType)
     * @see ResourceType#ORGANIZATION
     */
    public List<Application> getAllOrganizationsWaitingForApproval() {
        List<Application> applications =
                applicationManager.getAllPendingApplicationsOfResourceType(ResourceType.ORGANIZATION);
        return  applications;
    }
}
