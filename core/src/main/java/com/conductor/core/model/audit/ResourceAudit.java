package com.conductor.core.model.audit;

import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Resource Audit is used to maintain long states for 'a' resource.
 */
@Entity
@Table
public class ResourceAudit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by_id")
    private User lastUpdatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Lob
    @Column(name = "snapshot", nullable = false)
    private byte[] snapshot;

}
