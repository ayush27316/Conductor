package com.conductor.core.repository;

import com.conductor.core.model.application.Application;
import com.conductor.core.model.application.ApplicationStatus;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Long> {

    List<Application> findByTargetResource_ResourceTypeAndSubmittedBy(ResourceType resourceType, User submittedBy);

    List<Application> findByTargetResource(Resource resource);

    Optional<Application> findByExternalId(String externalId);
    List<Application> findByApplicationStatus(ApplicationStatus status);

    List<Application> findBySubmittedBy(User submittedBy);

    List<Application> findByTargetResource_ExternalId(String externalId);

    boolean existsBySubmittedByAndTargetResource(User user, Resource targetResource);

    List<Application> findByTargetResource_ResourceType(ResourceType resourceType);

    List<Application> findByTargetResource_ResourceTypeAndApplicationStatus(ResourceType resourceType, ApplicationStatus applicationStatus);
}
