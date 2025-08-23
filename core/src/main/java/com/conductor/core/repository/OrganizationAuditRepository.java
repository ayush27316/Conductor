package com.conductor.core.repository;

import com.conductor.core.model.audit.OrganizationAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationAuditRepository extends JpaRepository<OrganizationAudit, Long> {

}


