package com.conductor.core.repository;

import com.conductor.core.model.org.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByName(String name);

//    @Query("SELECT o FROM organizations o JOIN FETCH o.audit WHERE o.name = :name")
//    Optional<Organization> findWithAuditByName(@Param("name") String name);

}

