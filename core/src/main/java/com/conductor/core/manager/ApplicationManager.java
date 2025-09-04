package com.conductor.core.service;

import com.conductor.core.dto.ApplicationDTO;
import com.conductor.core.exception.*;

import com.conductor.core.model.common.Resource;
import com.conductor.core.model.form.Form;
import com.conductor.core.repository.*;
import com.conductor.core.util.ApplicationMapper;
import com.conductor.core.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides operations for creating, approving, rejecting, canceling,
 * and managing forms or comments for an applications.
 */
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final FormRepository formRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a new {@link Application} for the specified {@link User} and {@link Resource}
     * and registers/persists it.
     *
     * @param user           user creating the application (must not be {@code null} or transient )
     * @param targetResource resource being applied to (must not be {@code null} or transient )
     * @param form           optional form to attach to the application (if present then it must not be
     *                       a transient entity)
     *
     * @throws IllegalArgumentException             if user or resource is {@code null}
     * @throws ApplicationRequestFailedException    if an application for the same resource already exists
     *                                              or a database operations failed.
     */
    @Transactional
    public Application registerApplication(
            User user,
            Resource targetResource,
            Optional<Form> form
    ){
        notNull(user, "User cannot be null");
        notNull(targetResource, "Target resource cannot be null");

        // Check if user already has an application for this resource
        if (applicationRepository.existsBySubmittedByAndTargetResource(
                user, targetResource)) {
            throw new ApplicationRequestFailedException
                    ("You already have a pending application for this " +
                            targetResource.getResourceType().getName()
                    );
        }

        Application application = Application.createNewApplicaton(
                targetResource,
                user,
                form.isEmpty()? null : form.get());

        user.getApplications().add(application);

        try {
            userRepository.save(user);
        }catch (Exception e) {
            throw new ApplicationRequestFailedException("Application request failed",e);
        }
        return application;
    }

    /**
     * Approves an existing {@link Application}.
     *
     * @param user                   user approving the application (must not be {@code null} or transient)
     * @param applicationExternalId  external identifier of the application (must not be {@code null})
     * @throws ApplicationNotFound                  if the application cannot be found
     * @throws IllegalArgumentException             if arguments are {@code null}
     * @throws ApplicationRequestFailedException    for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public Application approveApplication(
            User user,
            String applicationExternalId)
    {

        notNull(user, "User cannot be null");
        notNull(applicationExternalId, "Application Id is required");

        Application application = findApplication(applicationExternalId);
        application.approve(user);
        saveOrElseThrow(application);

        return application;
    }

    /**
     * Adds a {@link Form} schema to an {@link Application}.
     *
     * @param applicationExternalId external identifier of the application (must not be {@code null})
     * @param formSchema            form schema JSON string
     * @throws ApplicationNotFound                  if the application cannot be found
     * @throws ApplicationRequestFailedException    for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public Application addForm(
            String applicationExternalId,
            String formSchema)
    {
        Application application = findApplication(applicationExternalId);
        Form form = formRepository.save(Form.createNew(formSchema));

        application.setApplicationForm(form);
        applicationRepository.save(application);
        saveOrElseThrow(application);
        return application;
    }

    /**
     * Submits a response for the application form.
     *
     * @param applicationExternalId external identifier of the application (must not be {@code null})
     * @param formResponse          response JSON string for the form (must not be {@code null})
     *
     * @throws FormNotFoundException       if the application does not have a form
     * @throws InvalidFormSubmissionException if the form response is invalid
     * @throws ApplicationNotFound         if the application cannot be found
     * @throws ApplicationRequestFailedException for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public Application submitApplicationFormResponse(
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
        saveOrElseThrow(application);
        return application;
    }

    /**
     * Cancels an existing {@link Application}.
     *
     * @param applicationExternalId external identifier of the application (must not be {@code null})
     * @throws ApplicationNotFound                      if the application cannot be found
     * @throws ApplicationRequestFailedException        for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public Application cancelEventApplication(
            String applicationExternalId)
    {
        notNull(applicationExternalId, "Application Id is required");

        Application application = findApplication(applicationExternalId);

        application.cancel();
        saveOrElseThrow(application);
        return application;
    }


    /**
     * Rejects an existing {@link Application} with a reason.
     *
     * @param user                   user rejecting the application (must not be {@code null})
     * @param applicationExternalId  external identifier of the application (must not be {@code null})
     * @param reason                 reason for rejection (must not be {@code null})
     * @throws ApplicationNotFound                      if the application cannot be found
     * @throws ApplicationRequestFailedException        for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public Application rejectEventApplication(
            User user,
            String applicationExternalId,
            String reason)
    {
        notNull(user, "User cannot be null");
        notNull(applicationExternalId, "Application Id is required");
        notNull(reason, "Rejection reason is required");

        Application application = findApplication(applicationExternalId);

        application.reject(user, reason);
        saveOrElseThrow(application);
        return application;
    }

    /**
     * Adds a comment to an existing {@link Application}.
     *
     * @param user                   user adding the comment (must not be {@code null})
     * @param applicationExternalId  external identifier of the application (must not be {@code null})
     * @param comment                comment text (must not be {@code null})
     * @throws ApplicationNotFound                  if the application cannot be found
     * @throws ApplicationRequestFailedException    for all other exceptions cause due to database operations failure.
     */
    @Transactional
    public Application addComment(
            User user,
            String applicationExternalId,
            String comment) {

        notNull(user, "User cannot be null");
        notNull(applicationExternalId, "Application Id is required");
        notNull(comment, "Comment is required");

        Application application = findApplication(applicationExternalId);

        application.addComment(user,comment);
        saveOrElseThrow(application);
        return application;
    }

    /**
     * Fetches all applications associated with a target resource.
     *
     * @param resourceExternalId external identifier of the resource
     * @return list of {@link Application} objects for the resource
     */
    @Transactional
    public List<Application> getAllApplicationsForAResource(String resourceExternalId) {
        try {
            return applicationRepository
                    .findByTargetResource_ExternalId(resourceExternalId);
        }catch (Exception e) {
            throw new ApplicationRequestFailedException("Application request failed",e);
        }
    }


    /**
    * Finds an application by its external ID.
    *
    * @param applicationExternalId external identifier of the application
    * @return the found {@link Application}
    * @throws ApplicationNotFound if the application cannot be found
    */
    public Application findApplication(String applicationExternalId) {
        Optional<Application> appOpt = applicationRepository.findByExternalId(applicationExternalId);
        if (appOpt.isEmpty()) {
            throw new ApplicationNotFound("Application not found");
        }
        return appOpt.get();
    }

    private void saveOrElseThrow(Application application)
    {
        try {
            applicationRepository.save(application);
        }catch (Exception e) {
            throw new ApplicationRequestFailedException("Application request failed",e);
        }
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

    private void notNull(Object ob, String message) {
        Utils.notNull(ob,message);
    }

    private void doIfPresent(Optional<Consumer<Application>> consumer, Application application) {
        if(!consumer.isEmpty()) {
            consumer.get().accept(application);
        }
    }

}
