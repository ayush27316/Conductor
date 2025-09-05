package com.conductor.core.model.audit;

import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Resource Audit is used to maintain long states for 'a' resource.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resource_audit")
public class ResourceAudit extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "resource_id_fk")
    private Resource resource;

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

    /**
     * It stores a snapshot of an event and all its associated resources.
     * We can do a diff and see who changes what and when.
     */
    @Lob
    @Column(name = "snapshot", nullable = false)
    private byte[] snapshot;

    @PrePersist
    public void doThis()
    {
        if (Objects.isNull(snapshot))
        {
            snapshot = new byte[5];

        }    }
}
