package com.conductor.core.repository;

import com.conductor.core.model.org.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByName(String name);

   // Optional<Organization> findByExternalId(String externalId);

}

