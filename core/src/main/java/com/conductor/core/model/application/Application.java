package com.conductor.core.model.application;

import com.conductor.core.model.common.Resource;
import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * A user submits an {@link Application} for creating resources
 * like {@link com.conductor.core.model.ticket.Ticket} for an
 * {@link com.conductor.core.model.event.Event} or for registering
 * a new {@link com.conductor.core.model.org.Organization }.
 *
 */
@Entity
@Table(name = "applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application extends BaseEntity {

    @Column(name = "external_id", unique = true, updatable = false, nullable = false)
    @Builder.Default
    private String externalId = UUID.randomUUID().toString();

    /**
     * An Application has a polymorphic association with resources in the system.
     * This could either be an {@link com.conductor.core.model.event.Event} or
     * {@link com.conductor.core.model.org.Organization}
     */
    @OneToOne
    @JoinColumn(name = "resource_id_fk", nullable = false)
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "submitted_by_user_id_fk", nullable = false)
    private User submittedBy;

    @Column(name = "submitted_at", nullable = false)
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "processed_by_user_id_fk")
    private User processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ApplicationComment> comments = new ArrayList<>();


    public void addComment(ApplicationComment comment) {
        comments.add(comment);
        comment.setApplication(this);
    }

    public void approve(User approver) {
        validateStatusTransition(ApplicationStatus.APPROVED);
        this.applicationStatus = ApplicationStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
        this.processedBy = approver;
    }


    public void reject(User rejector) {
        //we can add default comments for rejection
        validateStatusTransition(ApplicationStatus.REJECTED);
        this.applicationStatus = ApplicationStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
        this.processedBy = rejector;
    }

    /**
     * Cancel the registration (user action)
     */
    public void cancel() {
        validateStatusTransition(ApplicationStatus.CANCELLED);
        this.applicationStatus = ApplicationStatus.CANCELLED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Check if registration is pending
     */
    public boolean isPending() {
        return ApplicationStatus.PENDING.equals(this.applicationStatus);
    }

    /**
     * Check if registration is approved
     */
    public boolean isApproved() {
        return ApplicationStatus.APPROVED.equals(this.applicationStatus);
    }

    /**
     * Check if application is in a final state
     */
    public boolean isFinalStatus() {
        return this.applicationStatus != null && this.applicationStatus.isFinalStatus();
    }


    /**
     * Validate that status transition is allowed
     */
    private void validateStatusTransition(ApplicationStatus newStatus) {
        if (this.applicationStatus != null && this.applicationStatus.isFinalStatus()) {
            throw new IllegalStateException(
                    String.format("Cannot transition from final status %s to %s",
                            this.applicationStatus, newStatus));
        }

        if (!this.applicationStatus.canBeProcessed() && newStatus != ApplicationStatus.CANCELLED) {
            throw new IllegalStateException(
                    String.format("Invalid status transition from %s to %s",
                            this.applicationStatus, newStatus));
        }
    }

    /**
     * Pre-persist callback to ensure required fields are set
     */
    @PrePersist
    protected void prePersist() {
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
        if (applicationStatus == null) {
            applicationStatus = ApplicationStatus.PENDING;
        }
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }

}



