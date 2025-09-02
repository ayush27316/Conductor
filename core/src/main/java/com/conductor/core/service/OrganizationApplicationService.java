package com.conductor.core.service;

import com.conductor.core.dto.ApproveOrganizationRequest;
import com.conductor.core.dto.OrganizationRegistrationRequest;
import com.conductor.core.dto.OrganizationRegistrationResult;
import com.conductor.core.exception.ApplicationNotFound;
import com.conductor.core.exception.OrganizationApprovalException;
import com.conductor.core.exception.OrganizationRegistrationException;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.application.ApplicationStatus;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.org.OrganizationAudit;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.*;
import com.conductor.core.util.Pair;
import lombok.RequiredArgsConstructor;
import com.conductor.core.dto.FormSchemaRequest;
import com.conductor.core.dto.FormSchemaResponse;
import com.conductor.core.dto.FormSubmissionRequest;
import com.conductor.core.exception.FormNotFoundException;
import com.conductor.core.exception.InvalidFormSubmissionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                    .targetResource(organization)
                    .submittedBy(principal)
                    .build();

            applicationRepository.save(application);

            return OrganizationRegistrationResult.builder()
                    .success(true)
                    .registrationId(application.getExternalId())
                    .message("Organization registration successfully")
                    .build();

        } catch (Exception e) {
//            e.printStackTrace();
            throw new OrganizationRegistrationException("Organization registration failed." + e.getMessage(), e);
        }
    }


    private Pair<Boolean, String> isUserEligibleToCreateNewApplication(User user) {
        List<Application> applications = applicationRepository.findByTargetResource_ResourceTypeAndSubmittedBy(
                ResourceType.ORGANIZATION,
                getCurrentUser()
        );

        if(applications.isEmpty()){
            return Pair.of(true,"");
        }

        return Pair.of(false, "An organization application already exists.");

    }

    /**
     * Get the SurveyJS form schema for an application
     */
    public FormSchemaResponse getFormSchema(String applicationExternalId) {
        Application app = applicationRepository.findByExternalId(applicationExternalId)
                .orElseThrow(() -> new ApplicationNotFound("Application not found", new IllegalArgumentException()));

        if (app.getFormSchemaJson() == null || app.getFormSchemaJson().isEmpty()) {
            throw new FormNotFoundException("Form schema not configured for this application");
        }

        return FormSchemaResponse.builder().schemaJson(app.getFormSchemaJson()).build();
    }

    /**
     * Set or update the SurveyJS form schema for an application
     */
    @Transactional
    public void setFormSchema(String applicationExternalId, FormSchemaRequest request) {
        Application app = applicationRepository.findByExternalId(applicationExternalId)
                .orElseThrow(() -> new ApplicationNotFound("Application not found", new IllegalArgumentException()));

        validateJson(request.getSchemaJson(), "Invalid form schema JSON");
        app.setFormSchemaJson(request.getSchemaJson());
        applicationRepository.save(app);
    }

    /**
     * Submit SurveyJS form result for an application
     */
    @Transactional
    public void submitFormResult(String applicationExternalId, FormSubmissionRequest request) {
        Application app = applicationRepository.findByExternalId(applicationExternalId)
                .orElseThrow(() -> new ApplicationNotFound("Application not found", new IllegalArgumentException()));

        if (app.getFormSchemaJson() == null) {
            throw new FormNotFoundException("Form schema not configured for this application");
        }

        validateJson(request.getResultJson(), "Invalid form result JSON");
        app.setFormResultJson(request.getResultJson());
        applicationRepository.save(app);
    }

    private void validateJson(String json, String errorMessage) {
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node == null || node.isNull()) {
                throw new InvalidFormSubmissionException(errorMessage);
            }
        } catch (Exception e) {
            throw new InvalidFormSubmissionException(errorMessage);
        }
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

            initiateOrganizationOnboarding((Organization) application.getTargetResource());

        }catch (Exception e )
        {

            throw new OrganizationApprovalException("Organization Approval failed. Please try again" + e.getMessage(), e);
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejectOrganization(String applicationExternalId, String rejectionReason) {

        User principal = getCurrentUser();

        Optional<Application> applicationOptional = applicationRepository.findByExternalId(applicationExternalId);

        if(applicationOptional.isEmpty()){
            throw new ApplicationNotFound("Registration Id is not valid.", new IllegalArgumentException());
        }

        Application application = applicationOptional.get();

        if (application.isFinalStatus()) {
            throw new IllegalArgumentException("Application is already in a final state. Cannot proceed with the request", new IllegalStateException());
        }

        try{
            application.reject(principal, rejectionReason);
            applicationRepository.save(application);

        }catch (Exception e )
        {
            throw new RuntimeException("Organization Rejection failed. Please try again" + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addComment(String applicationExternalId, String comment) {

        User principal = getCurrentUser();

        Optional<Application> applicationOptional = applicationRepository.findByExternalId(applicationExternalId);

        if(applicationOptional.isEmpty()){
            throw new ApplicationNotFound("Registration Id is not valid.", new IllegalArgumentException());
        }

        Application application = applicationOptional.get();

        try{
            application.addComment(principal, comment);
            applicationRepository.save(application);

        }catch (Exception e )
        {
            throw new RuntimeException("Add Comment failed. Please try again" + e.getMessage(), e);
        }

    }


    private void addFile(MultipartFile file) throws IOException {
        //add comment
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        byte[] data = file.getBytes();

        //file.service()

    }

//    @PostMapping("/files")
//    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file,
//                                              @RequestParam("fileType") FileType fileType,
//                                              @RequestParam("name") String name) throws IOException {
//        File savedFile = fileService.saveFile(file, name, fileType);
//        return ResponseEntity.ok(fileMapper.toDto(savedFile));
//    }
//
//
//    @GetMapping("/files/{id}/download")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String id) {
//        File file = fileService.getFileById(id);
//
//        ByteArrayResource resource = new ByteArrayResource(file.getFileDataBlob());
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
//                .contentType(MediaType.parseMediaType(file.getMimeType()))
//                .contentLength(file.getFileSize())
//                .body(resource);
//    }

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

        try {
            List<Application> registration = applicationRepository.findByApplicationStatus(ApplicationStatus.PENDING);
            return registration;
        } catch (Exception e)
        {
            throw new RuntimeException("Internal Server Error");
        }
    }
//
//    // Form helpers for Organization Applications
//    public FormSchemaResponse getFormSchema(String applicationExternalId) {
//        Application app = applicationRepository.findByExternalId(applicationExternalId)
//                .orElseThrow(() -> new ApplicationNotFound("Application not found", new IllegalArgumentException()));
//        if (app.getFormSchemaJson() == null || app.getFormSchemaJson().isEmpty()) {
//            throw new FormNotFoundException("Form schema not configured for this application");
//        }
//        return FormSchemaResponse.builder().schemaJson(app.getFormSchemaJson()).build();
//    }
//
//    @Transactional
//    public void setFormSchema(String applicationExternalId, FormSchemaRequest request) {
//        Application app = applicationRepository.findByExternalId(applicationExternalId)
//                .orElseThrow(() -> new ApplicationNotFound("Application not found", new IllegalArgumentException()));
//        validateJson(request.getSchemaJson(), "Invalid form schema JSON");
//        app.setFormSchemaJson(request.getSchemaJson());
//        applicationRepository.save(app);
//    }
//
//    @Transactional
//    public void submitFormResult(String applicationExternalId, FormSubmissionRequest request) {
//        Application app = applicationRepository.findByExternalId(applicationExternalId)
//                .orElseThrow(() -> new ApplicationNotFound("Application not found", new IllegalArgumentException()));
//        if (app.getFormSchemaJson() == null) {
//            throw new FormNotFoundException("Form schema not configured for this application");
//        }
//        validateJson(request.getResultJson(), "Invalid form result JSON");
//        app.setFormResultJson(request.getResultJson());
//        applicationRepository.save(app);
//    }
}
