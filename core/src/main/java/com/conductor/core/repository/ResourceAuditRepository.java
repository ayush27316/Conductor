package com.conductor.core.repository;

import com.conductor.core.model.audit.ResourceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceAuditRepository extends JpaRepository<ResourceAudit, Long> {

}
