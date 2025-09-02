package com.conductor.core.model.application;

import com.conductor.core.model.common.Resource;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.file.File;
import com.conductor.core.model.form.Form;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
public class Application extends Resource {

    /**
     * An Application has a polymorphic association with resources in the system.
     * This could either be an {@link com.conductor.core.model.event.Event} or
     * {@link com.conductor.core.model.org.Organization}
     */
    @OneToOne
    @JoinColumn(name = "target_resource_id_fk", nullable = false)
    private Resource targetResource;

    @ManyToOne
    @JoinColumn(name = "submitted_by_user_id_fk", nullable = false)
    private User submittedBy;

    @Column(name = "submitted_at", nullable = false)
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

    @ManyToOne(optional = false)
    @Column(name = "processed_by")
    private User processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

//    @OneToMany(mappedBy = "resource")
//    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @OrderBy("createdAt ASC")
    private List<ApplicationComment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "application_form")
    private Form applicationForm;

    @Lob
    @Column(name = "application_form_response")
    private String applicationFormResponse;

    public void addComment(ApplicationComment comment) {
        comments.add(comment);
        comment.setApplication(this);
    }

    public boolean addFile(File file)
    {
//        if (Objects.isNull(file)) return false;
//        files.add(file);
        return true;
    }

    public boolean addComment(User author, String message)
    {
        if(Objects.isNull(message))
        {
            return false;
        }

        ApplicationComment newComment =
                ApplicationComment.builder()
                        .application(this)
                        .author(author)
                        .content(message) //change this from configuration set message
                        .build();

        comments.add(newComment);
        return true;
    }

    public void approve(User approver) {

        validateStatusTransition(ApplicationStatus.APPROVED);
        this.applicationStatus = ApplicationStatus.APPROVED;
        processedBy = approver;
        processedAt = LocalDateTime.now();

    }

    public static Application createNewApplicatonForEvent(
        Event event,
        User submittedBy)
    {
        if(!event.requiresApplication())
        {return null;}

        return Application.builder()
                .targetResource(event)
                .submittedBy(submittedBy)
                .applicationStatus(ApplicationStatus.PENDING)
                .processedAt(LocalDateTime.now())
                .applicationForm(event.getApplicationForm())
                .build();
    }

    public void reject(User rejector, String rejectionReason) {

        validateStatusTransition(ApplicationStatus.REJECTED);
        this.applicationStatus = ApplicationStatus.REJECTED;

        ApplicationComment newComment =
                ApplicationComment.builder()
                        .application(this)
                        .author(rejector)
                        .content(rejectionReason != null ? rejectionReason: "We are sorry for you loss") //change this from configuration set message
                        .build();

        comments.add(newComment);
    }

    /**
     * Cancel the registration. Suppose to be done by user that
     * submitted the application
     */
    public void cancel() {
        validateStatusTransition(ApplicationStatus.CANCELLED);
        this.applicationStatus = ApplicationStatus.CANCELLED;
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
        super.init(ResourceType.APPLICATION);
        if (applicationStatus == null) {
            applicationStatus = ApplicationStatus.PENDING;
        }
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }
    public boolean hasUserSubmittedForm() {
        return !Objects.isNull(applicationFormResponse);
    }

    public boolean hasForm() {
        return Objects.isNull(applicationForm);
    }
}



