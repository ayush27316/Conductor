package com.conductor.core.service;

import com.conductor.core.dto.ApproveOrganizationRequest;
import com.conductor.core.dto.OrganizationRegistrationRequest;
import com.conductor.core.dto.OrganizationRegistrationResult;
import com.conductor.core.exception.OrganizationApprovalException;
import com.conductor.core.exception.OrganizationRegistrationException;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.application.ApplicationStatus;
import com.conductor.core.model.org.OrganizationPrivilege;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.util.OrganizationMapper;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.org.OrganizationAudit;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.*;
import com.conductor.core.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrganizationRegistrationService {

    private final ApplicationRepository applicationRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationAuditRepository auditRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /*
    * registerOrganization is the first step before an organization can
    * create events. Once operators from conductor org approves the reservation
    * organization onboarding is initiated. A user can only register for a
    * single organization at a time.
    *
    * */
    @Transactional
    public OrganizationRegistrationResult registerOrganization(OrganizationRegistrationRequest request) {

        User principal = getCurrentUser();

        Pair<Boolean, String> result =
                isUserEligibleToCreateNewApplication(principal);

        if (!result.getStatus()) {
            return OrganizationRegistrationResult.builder()
                    .success(false)
                    .message(result.getMessage())
                    .build();
        }

        //check if organization name already exist
        if(!organizationRepository.findByName(request.getName()).isEmpty()){
            throw new RuntimeException("Organization name already taken");
        }

//        //check if email is valid
//        if(!validateEmail(request.getEmail()))
//        {
//            throw InvalidEmailException.standard();
//        }

        try {
            Organization organization = Organization.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .description(request.getDescription())
                    .locations(request.getLocations())
                    .websiteUrl(request.getWebsiteUrl())
                    .tags(request.getTags())
                    .build();

            organizationRepository.save(organization);

            Application application = Application.builder()
                    .resource(organization)
                    .submittedBy(principal)
                    .build();

            applicationRepository.save(application);

            return OrganizationRegistrationResult.builder()
                    .success(true)
                    .registrationId(application.getExternalId())
                    .message("Organization registration successfully")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new OrganizationRegistrationException("Organization registration failed." + e.getMessage(), e);
        }
    }


    private Pair<Boolean, String> isUserEligibleToCreateNewApplication(User user) {
        List<Application> applications = applicationRepository.findByResource_ResourceTypeAndSubmittedBy(
                ResourceType.ORGANIZATION,
                getCurrentUser()
        );

        if(applications.isEmpty()){
            return Pair.of(true,"");
        }

        return Pair.of(false, "An organization application already exists.");

    }

    /**
     * Approve reservation and initiate organization onboarding
     * Returns true if successful, false if reservation is not for organization onboarding
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean approveOrganization(ApproveOrganizationRequest request) {

        User principal = getCurrentUser();

        Optional<Application> applicationOptional = applicationRepository.findByExternalId(request.getRegistrationId());

        if(applicationOptional.isEmpty()){
            throw new OrganizationRegistrationException("Registration Id is not valid.", new IllegalArgumentException());
        }

        Application application = applicationOptional.get();

        if (application.isApproved()) {
            throw new OrganizationRegistrationException("Organization is already approved", new IllegalStateException());
        }

        try{
            application.approve(principal);

            applicationRepository.save(application);

            initiateOrganizationOnboarding((Organization) application.getResource());

        }catch (Exception e )
        {
            e.printStackTrace();
            throw new OrganizationApprovalException("Organization Approval failed. Please try again" + e.getMessage(), e);
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    private void initiateOrganizationOnboarding(Organization org){
        OrganizationAudit audit = OrganizationAudit.getBlankAudit(org);

        Map<String, String> privileges = OrganizationPrivilege.getOwnerPrivileges();

        Permission permission = Permission.builder()
                .resource(org)
                .privileges(privileges).build();

        organizationRepository.save(org);
        auditRepository.save(audit);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
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

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Invalid authentication context");
        }

        return  (User) authentication.getPrincipal();
    }

    /**
     * Get all organizations waiting for approval
     */
    public List<Application> getAllOrganizationsWaitingForApproval() {

        List<Application> registration =  applicationRepository.findByApplicationStatus(ApplicationStatus.PENDING);
        return registration;

    }
}
