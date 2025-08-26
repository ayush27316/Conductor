package com.conductor.core.model.org;

import com.conductor.core.model.permission.BaseEntity;
import com.conductor.core.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents an organization registration request submitted by a user.
 * Tracks the approval workflow for users requesting to create organizations.
 */
@Entity
@Table(
        name = "organization_registrations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_registration_id", columnNames = "registration_id"),
                @UniqueConstraint(name = "uk_user_pending", columnNames = {"user_id_fk", "status"})
        },
        indexes = {
                @Index(name = "idx_org_reg_user_status", columnList = "user_id_fk, status"),
                @Index(name = "idx_org_reg_status_submitted", columnList = "status, submitted_at"),
                @Index(name = "idx_org_reg_organization", columnList = "organization_id_fk")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"user", "organization"})
@ToString(exclude = {"user", "organization"})
public class OrganizationRegistration extends BaseEntity {

    @Builder.Default
    @Column(name = "registration_id", unique = true, nullable = false, updatable = false, length = 36)
    private String registrationId = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id_fk", nullable = false,
            foreignKey = @ForeignKey(name = "fk_org_reg_user"))
    @JsonIgnore
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "organization_id_fk", nullable = false,
            foreignKey = @ForeignKey(name = "fk_org_reg_organization"))
    private Organization organization;

    @Column(name = "submitted_at", nullable = true, updatable = false)
    @Builder.Default
    private Instant submittedAt = Instant.now();

    @Column(name = "processed_at")
    private Instant processedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id_fk",
            foreignKey = @ForeignKey(name = "fk_org_reg_processed_by"))
    @JsonIgnore
    private User processedBy;

    @Column(name = "processing_notes", length = 1000)
    private String processingNotes;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    /**
     * Organization registration status enum with improved functionality
     */
    public enum Status {
        PENDING("pending"),
        APPROVED("approved"),
        REJECTED("rejected"),
        CANCELLED("cancelled");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * Get Status enum from string value
         * @param value the string value
         * @return Optional containing the Status if found
         */
        public static Optional<Status> fromValue(String value) {
            if (value == null || value.trim().isEmpty()) {
                return Optional.empty();
            }

            return Arrays.stream(values())
                    .filter(status -> status.value.equalsIgnoreCase(value.trim()))
                    .findFirst();
        }

        /**
         * Check if status represents a final state (no further transitions expected)
         */
        public boolean isFinalStatus() {
            return this == APPROVED || this == REJECTED || this == CANCELLED;
        }

        /**
         * Check if status allows processing
         */
        public boolean canBeProcessed() {
            return this == PENDING;
        }
    }

    /**
     * Approve the registration
     */
    public void approve(User approver, String notes) {
        validateStatusTransition(Status.APPROVED);
        this.status = Status.APPROVED;
        this.processedAt = Instant.now();
        this.processedBy = approver;
        this.processingNotes = notes;
        this.rejectionReason = null; // Clear any previous rejection reason
    }

    /**
     * Reject the registration
     */
    public void reject(User rejector, String reason, String notes) {
        validateStatusTransition(Status.REJECTED);
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }

        this.status = Status.REJECTED;
        this.processedAt = Instant.now();
        this.processedBy = rejector;
        this.rejectionReason = reason.trim();
        this.processingNotes = notes;
    }

    /**
     * Cancel the registration (user action)
     */
    public void cancel(String notes) {
        validateStatusTransition(Status.CANCELLED);
        this.status = Status.CANCELLED;
        this.processedAt = Instant.now();
        this.processingNotes = notes;
    }

    /**
     * Check if registration is pending
     */
    public boolean isPending() {
        return Status.PENDING.equals(this.status);
    }

    /**
     * Check if registration is approved
     */
    public boolean isApproved() {
        return Status.APPROVED.equals(this.status);
    }

    /**
     * Check if registration is in a final state
     */
    public boolean isFinalStatus() {
        return this.status != null && this.status.isFinalStatus();
    }

    /**
     * Get user ID for queries without loading the full user entity
     */
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    /**
     * Get organization ID for queries without loading the full organization entity
     */
    public Long getOrganizationId() {
        return organization != null ? organization.getId() : null;
    }

    /**
     * Validate that status transition is allowed
     */
    private void validateStatusTransition(Status newStatus) {
        if (this.status != null && this.status.isFinalStatus()) {
            throw new IllegalStateException(
                    String.format("Cannot transition from final status %s to %s",
                            this.status, newStatus));
        }

        if (!this.status.canBeProcessed() && newStatus != Status.CANCELLED) {
            throw new IllegalStateException(
                    String.format("Invalid status transition from %s to %s",
                            this.status, newStatus));
        }
    }

    /**
     * Pre-persist callback to ensure required fields are set
     */
    @PrePersist
    protected void prePersist() {
        if (registrationId == null) {
            registrationId = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = Status.PENDING;
        }
        if (submittedAt == null) {
            submittedAt = Instant.now();
        }
    }

    /**
     * Validation method that can be called before save operations
     */
    public void validate() {
        if (user == null) {
            throw new IllegalStateException("User is required");
        }
        if (organization == null) {
            throw new IllegalStateException("Organization is required");
        }
        if (status == null) {
            throw new IllegalStateException("Status is required");
        }
    }
}