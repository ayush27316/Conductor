package com.conductor.core.repository;

import com.conductor.core.model.application.Application;
import com.conductor.core.model.application.ApplicationStatus;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Long> {

    List<Application> findByResource_ResourceTypeAndSubmittedBy(ResourceType resourceType, User submittedBy);
    Optional<Application> findByExternalId(String externalId);
    List<Application> findByApplicationStatus(ApplicationStatus status);
}
