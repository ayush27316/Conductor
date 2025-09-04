package com.conductor.core.service;

import com.conductor.core.dto.OrganizationRegistrationRequest;
import com.conductor.core.exception.OrganizationRegistrationException;
import com.conductor.core.manager.ApplicationManager;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.org.OrganizationAudit;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.*;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * {@link Organization} onboarding starts with creating an {@link Application}
 * for it. Admin users can then interact with the user that made the request using
 * this Application.
 */
@Service
@RequiredArgsConstructor
public class OrganizationApplicationService {

    private final ApplicationRepository applicationRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationAuditRepository auditRepository;
    private final UserRepository userRepository;
    private final ApplicationManager applicationManager;

    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Transactional
    public String register(
            User submittedBy,
            OrganizationRegistrationRequest request) {

        //check if organization name already exist
        if(!organizationRepository.findByName(request.getName()).isEmpty()){
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
        try{
            organizationRepository.save(organization);
        }catch (Exception e) {
            throw new OrganizationRegistrationException("Organization registration failed." + e.getMessage(), e);
        }

        Application application = applicationManager.registerApplication(
                submittedBy,
                organization,
                null
                );

        return application.getExternalId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void approve(
            User approvedBy,
            String applicationExternalId) {

        Application application =
                applicationManager.approveApplication(approvedBy, applicationExternalId);

        initiateOrganizationOnboarding((Organization) application.getTargetResource());
    }

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

    @Transactional
    public void cancel(String applicationExternalId) {
        applicationManager.cancelEventApplication(applicationExternalId);
    }

    @Transactional
    public void comment(
            User user,
            String applicationExternalId,
            String comment) {
        applicationManager.addComment(user,applicationExternalId,comment);
    }

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


        permission.setUser(user);
        user.setPermissions(List.of(permission));

        userRepository.save(user);
        // TODO: Implement async call to email service to send credentials
        // transactions should be maintained if emailing fails
    }

    /**
     * Get all organizations waiting for approval
     */
    public List<Application> getAllOrganizationsWaitingForApproval() {
        return  applicationManager.getAllPendingApplicationsOfResourceType(ResourceType.ORGANIZATION);
    }
}
