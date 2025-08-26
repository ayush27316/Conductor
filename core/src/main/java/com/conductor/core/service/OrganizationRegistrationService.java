package com.conductor.core.service;

import com.conductor.core.dto.ApproveOrganizationRequest;
import com.conductor.core.dto.OrganizationRegistrationRequest;
import com.conductor.core.dto.OrganizationRegistrationResult;
import com.conductor.core.dto.auth.SignUpRequestDTO;
import com.conductor.core.exception.OrganizationApprovalException;
import com.conductor.core.exception.OrganizationRegistrationException;
import com.conductor.core.model.org.OrganizationPrivilege;
import com.conductor.core.model.org.OrganizationRegistration;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.permission.Resource;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.util.OrganizationMapper;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.org.OrganizationAudit;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.*;
import com.conductor.core.util.Pair;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    private final OrganizationRegistrationRepository organizationRegistrationRepository;
    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationAuditRepository auditRepository;
    private final TicketReservationRepository ticketReservationRepository;
    private final UserRepository userRepository;
    private final OrganizationMapper organizationMapper;
    private final AuthenticationService authenticationService;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Invalid authentication context");
        }

        User principal = (User) authentication.getPrincipal();

        if (userHasPendingOrganizationRegistration(principal)) {
            return OrganizationRegistrationResult.builder()
                    .success(false)
                    .message("User already has a pending organization registration.")
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

            OrganizationRegistration registration = OrganizationRegistration.builder()
                    .organization(organization)
                    .user(principal)
                    .status(OrganizationRegistration.Status.PENDING)
                    .build();

            registration = organizationRegistrationRepository.save(registration);

            return OrganizationRegistrationResult.builder()
                    .success(true)
                    .registrationId(registration.getRegistrationId())
                    .message("Organization registration submitted successfully")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new OrganizationRegistrationException("Organization registration failed." + e.getMessage(), e);
        }
    }


    private boolean userHasPendingOrganizationRegistration(User user) {
        // Use exists query instead of loading full entities
        return organizationRegistrationRepository.existsByUserAndStatus(
                user,
                OrganizationRegistration.Status.PENDING
        );
    }

    /**
     * Approve reservation and initiate organization onboarding
     * Returns true if successful, false if reservation is not for organization onboarding
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean approveOrganization(ApproveOrganizationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Invalid authentication context");
        }

        User principal = (User) authentication.getPrincipal();

        Optional<OrganizationRegistration> registrationOpt = organizationRegistrationRepository.findByRegistrationId(request.getRegistrationId());

        if(registrationOpt.isEmpty()){
            throw new OrganizationRegistrationException("Registration Id is not valid.", new IllegalArgumentException());
        }

        OrganizationRegistration registration = registrationOpt.get();

        if (registration.getStatus() == OrganizationRegistration.Status.APPROVED) {
            throw new OrganizationRegistrationException("Organization is already approved", new IllegalStateException());
        }

        try{
            registration.approve(principal, request.getNote());

            organizationRegistrationRepository.save(registration);

            initiateOrganizationOnboarding(registration.getOrganization());

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
                .resourceName(Resource.ORGANIZATION.getName())
                .resourceId(org.getExternalId())
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


    /**
     * Get all organizations waiting for approval
     */
    public List<OrganizationRegistration> getAllOrganizationsWaitingForApproval() {

        List<OrganizationRegistration> registration =  organizationRegistrationRepository.findByStatus(OrganizationRegistration.Status.PENDING);
        return registration;

    }
}
