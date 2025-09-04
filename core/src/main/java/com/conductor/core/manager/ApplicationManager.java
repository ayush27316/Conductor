
package com.conductor.core.manager;

import com.conductor.core.exception.*;
import com.conductor.core.model.application.ApplicationStatus;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.repository.*;
import com.conductor.core.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.user.User;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.List;
import java.util.Optional;

/**
 * Provides operations for creating, approving, rejecting, canceling,
 * and managing forms or comments for an applications. The operations
 * are also persisted.
 */
@RequiredArgsConstructor
public class ApplicationManager {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final FormRepository formRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a new {@link Application} for the specified {@link User} and {@link Resource}
     * and persists it.
     *
     *
     * @param user           user creating the application (must not be {@code null} or transient ).
     * @param targetResource resource being applied to (must not be {@code null} or transient ).
     * //@param form          form to attach to the application must not be {@code null} or transient.
     * @param formResponse    optional response of the form. Must be validated by the callee.
     *
     * @throws IllegalArgumentException             if any argument is {@code null}
     * @throws ApplicationRequestFailedException    if an application by the user for the same
     *                                              resource already exists or a database operations
     *                                              failed due to arguments being null or transient.
     */
    public Application registerApplication(
            User user,
            Resource targetResource,
            String formResponse
    ){
        //no need to check as an exception will be thrown when
        //trying to persist PersistenceException.
        //        notNull(user, "User cannot be null");
//        notNull(targetResource, "Target resource cannot be null");

        // Check if user already has an application for this resource
        if (applicationRepository.existsBySubmittedByAndTargetResource(
                user, targetResource)) {
            throw new ApplicationRequestFailedException
                    ("You already have a pending application for this " +
                            targetResource.getResourceType().getName()
                    );
        }

        Application application = Application.createNew(
                targetResource,
                user,
//                form,
                formResponse);

        user.getApplications().add(application);

        try {
            userRepository.save(user);
        }catch (Exception e) {
            throw new ApplicationRequestFailedException("Application request failed",e);
        }
        return application;
    }

    /**
     * Approves an existing {@link Application} then persists the change.
     *
     * @param approvedBy                   user approving the application (must not be {@code null} or transient)
     * @param applicationExternalId  external identifier of the application (must not be {@code null})
     *
     * @throws ApplicationNotFound                  if the application cannot be found
     * @throws IllegalArgumentException             if arguments are {@code null}
     * @throws IllegalStateException                if application is in final state. See {@link Application#isFinalStatus()}
     * @throws ApplicationRequestFailedException    for all other exceptions cause due to database operations failure.
     */
    public Application approveApplication(
            User approvedBy,
            String applicationExternalId)
    {

//        notNull(user, "User cannot be null");
//        notNull(applicationExternalId, "Application Id is required");

        Application application = findApplication(applicationExternalId);
        if(application.isApproved()) {
            throw new ApplicationRequestFailedException("Application is already approved");
        }
        application.approve(approvedBy);
        saveOrElseThrow(application);

        return application;
    }

    /**
     * Cancels an existing {@link Application}.
     *
     * @param applicationExternalId external identifier of the application (must not be {@code null})
     *
     * @throws ApplicationNotFound                  if the application cannot be found
     * @throws ApplicationRequestFailedException    for all other exceptions cause due to database operations failure.
     * @throws IllegalStateException                if application is in final state. See {@link Application#isFinalStatus()}
     */
    public Application cancelEventApplication(
            String applicationExternalId)
    {
//        notNull(applicationExternalId, "Application Id is required");

        Application application = findApplication(applicationExternalId);

        application.cancel();
        saveOrElseThrow(application);
        return application;
    }


    /**
     * Rejects an existing {@link Application} with a reason and then persists it.
     *
     * @param user                   user rejecting the application (must not be {@code null})
     * @param applicationExternalId  external identifier of the application (must not be {@code null})
     * @param reason                 reason for rejection (must not be {@code null})
     *
     * @throws ApplicationNotFound                      if the application cannot be found
     * @throws ApplicationRequestFailedException        for all other exceptions cause due to database operations failure.
     * @throws IllegalStateException                    if application is in final state. See {@link Application#isFinalStatus()}
     */
    public Application rejectEventApplication(
            User user,
            String applicationExternalId,
            String reason)
    {
//        notNull(user, "User cannot be null");
//        notNull(applicationExternalId, "Application Id is required");
        notNull(reason, "Rejection reason is required");

        Application application = findApplication(applicationExternalId);

        if (application.isFinalStatus()) {
            throw new ApplicationRequestFailedException(
                    "Application is " + application.getApplicationStatus().getName(),
                    new IllegalStateException());
        }

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
     *
     * @throws ApplicationNotFound                  if the application cannot be found
     * @throws ApplicationRequestFailedException    for all other exceptions cause due to database operations failure.
     */
    public Application addComment(
            User user,
            String applicationExternalId,
            String comment) {

//        notNull(user, "User cannot be null");
//        notNull(applicationExternalId, "Application Id is required");
//        notNull(comment, "Comment is required");

        Application application = findApplication(applicationExternalId);

        application.putComment(user,comment);
        saveOrElseThrow(application);
        return application;
    }

    /**
     * Fetches all applications associated with a target resource.
     *
     * @param resourceExternalId external identifier of the resource
     * @return list of {@link Application} objects for the resource
     * @throws ApplicationRequestFailedException  if database operation failed
     */
    public List<Application> getAllApplicationsForAResource(String resourceExternalId) {
        try {
            return applicationRepository
                    .findByTargetResource_ExternalId(resourceExternalId);
        }catch (Exception e) {
            throw new ApplicationRequestFailedException("Application request failed",e);
        }
    }

    /**
     * Fetches all applications associated with a target resource.
     *
     * @param resourceType type of the resource
     * @return list of {@link Application} objects for the resource
     * @throws ApplicationRequestFailedException  if database operation failed
     */
    public List<Application> getAllPendingApplicationsOfResourceType(ResourceType resourceType) {
        try {
            return applicationRepository
                    .findByTargetResource_ResourceTypeAndApplicationStatus(resourceType, ApplicationStatus.PENDING);
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

    private void notNull(Object ob, String message) {
        Utils.notNull(ob,message);
    }

    private void doIfPresent(Optional<Consumer<Application>> consumer, Application application) {
        if(!consumer.isEmpty()) {
            consumer.get().accept(application);
        }
    }

}
